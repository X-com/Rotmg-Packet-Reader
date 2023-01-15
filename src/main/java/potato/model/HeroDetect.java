package potato.model;

import packets.data.GroundTileData;
import packets.data.ObjectData;
import packets.data.ObjectStatusData;
import potato.data.HeroState;
import potato.data.HeroType;
import potato.data.IdData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class HeroDetect {

    private final DataModel model;

    private final HashMap<Integer, ObjectData> entitys = new HashMap<>();
    private final int[][] mapTiles = new int[2048][2048];

    public HeroDetect(DataModel model) {
        this.model = model;
    }

    public HeroLocations findClosestHero(int x, int y) {
        return findClosestHero(x, y, model.mapCoords());
    }

    public HeroLocations findClosestHero(int x, int y, ArrayList<HeroLocations> list) {
        HeroLocations hero = null;
        float dist = Float.MAX_VALUE;
        for (HeroLocations h : list) {
            float d = h.squareDistTo(x, y);
            if (d < dist) {
                h.dist = d;
                dist = d;
                hero = h;
            }
        }
        return hero;
    }

    private ArrayList<HeroLocations> getCloseHeroListForId(int x, int y) {
        ArrayList<HeroLocations> list = new ArrayList<>();
        for (HeroLocations h : model.mapCoords()) {
            if (h.hasType()) continue;
            float d = h.squareDistTo(x, y);
            int CLOSEST_HERO_LIMIT = 3000;
            if (d < CLOSEST_HERO_LIMIT) {
                list.add(h);
            }
        }
        return list;
    }

    public void updateLocations(GroundTileData[] tiles, ObjectData[] newObjects, int[] drops) {
        if (!model.inRealm()) return;

        ArrayList<HeroLocations> nearHeroes = getCloseHeroListForId(model.getIntPlayerX(), model.getIntPlayerY());
        tileChecks(tiles, nearHeroes);
        entityChecks(newObjects, nearHeroes);
        checkMissing(nearHeroes);
        for (int drop : drops) {
            entitys.remove(drop);
        }
    }

    private void checkMissing(ArrayList<HeroLocations> nearHeroes) {
        HeroLocations h = findClosestHero(model.getIntPlayerX(), model.getIntPlayerY());
        if (h.isMissing(entitys)) {
            markDead(h);
        }
    }

    private void tileChecks(GroundTileData[] tiles, ArrayList<HeroLocations> nearHeroes) {
        for (GroundTileData gtd : tiles) {
            mapTiles[gtd.x][gtd.y] = gtd.type;

            if (nearHeroes.size() == 0) continue;

            HeroType hero = null;
            float dist = 0;

            if (gtd.type == IdData.SNAKE_STONE_TILE) {
                hero = HeroType.SNAKE;
                dist = 1000;
            } else if (gtd.type == IdData.LICH_BLUE_TILE) {
                hero = HeroType.LICH;
                dist = 500;
            } else if (gtd.type == IdData.DEMON_LAVA_TILE) {
                hero = HeroType.DEMON;
                dist = 500;
            } else if (gtd.type == IdData.PHENIX_BLACK_TILE) {
                if (phoenixTileCheck(gtd.x, gtd.y)) {
                    hero = HeroType.PHENIX;
                    dist = 250;
                }
            } else if (gtd.type == IdData.PARASITE_REDISH_TILE) {
                hero = HeroType.PARASITE;
                dist = 500;
            }

            if (hero != null) {
                HeroLocations h = findClosestHero(gtd.x, gtd.y, nearHeroes);
                if (h.dist < dist) {
                    nearHeroes.remove(h);
                    h.setType(hero);
                    switch (hero) {
                        case PARASITE:
                        case SNAKE:
                            markDead(h);
                            break;
                        default:
                            markVisited(h);
                    }
                }
            }
        }
    }

    private boolean phoenixTileCheck(short x, short y) {
        if (mapTiles[x + 7][y] == IdData.PHENIX_BLACK_TILE) return true;
        if (mapTiles[x - 7][y] == IdData.PHENIX_BLACK_TILE) return true;
        if (mapTiles[x][y + 7] == IdData.PHENIX_BLACK_TILE) return true;
        return mapTiles[x][y - 7] == IdData.PHENIX_BLACK_TILE;
    }

    private void entityChecks(ObjectData[] newObjects, ArrayList<HeroLocations> nearHeroes) {
        boolean wallAdded = false;
        for (ObjectData od : newObjects) {
            if (nearHeroes.size() > 0) {
                HeroLocations h = null;
                if (od.objectType == IdData.ENT_CHERRY_TREE) {
                    h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y, nearHeroes);
                    if (h.dist < 500) {
                        h.setType(HeroType.ENT);
                    }
                } else if (od.objectType == IdData.GRAVEYARD_CROSS) {
                    h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y, nearHeroes);
                    if (h.dist < 500) {
                        h.setType(HeroType.GRAVE);
                    }
                } else if (od.objectType == IdData.WOODEN_WALL_HOUSE) {
                    h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y, nearHeroes);
                    if (h.dist < 500) {
                        h.setType(HeroType.HOUSE);
                    }
                } else if (od.objectType == IdData.DEATH_TREE_MANOR) {
                    h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y, nearHeroes);
                    if (h.dist < 700) {
                        h.setType(HeroType.MANOR);
                    }
                } else if (od.objectType == IdData.LILLYPAD) {
                    h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y, nearHeroes);
                    if (h.dist < 300) {
                        h.setType(HeroType.OASIS);
                    }
                } else if (od.objectType == IdData.GHOST_INVON || od.objectType == IdData.GHOST_KILLABLE) {
                    h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y, nearHeroes);
                    if (h.dist < 300) {
                        h.setType(HeroType.GHOST);
                    }
                } else if (od.objectType == IdData.DESTRUCTIBLE_GRAY_WALL || od.objectType == IdData.GRAY_WALL) {
                    entitys.put(od.status.objectId, od);
                    wallAdded = true;
                    h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y, nearHeroes);
                    if (h.dist < 500) {
                        if (isCyclopsLoc((int) od.status.pos.x, (int) od.status.pos.y)) {
                            h.setType(HeroType.CYCLOPS);
                        }
                    }
                }

                if (h != null && h.hasType()) {
                    switch (h.getHeroType()) {
                        case GRAVE:
                        case HOUSE:
                        case MANOR:
                            markDead(h);
                            break;
                        default:
                            markVisited(h);
                    }
                    nearHeroes.remove(h);
                }
            }

            if (od.objectType == IdData.ENT_SMALL) {
                entitys.put(od.status.objectId, od);
            } else if (od.objectType == IdData.ENT_BIG) {
                entitys.put(od.status.objectId, od);
                HeroLocations h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y);
                markActive(h);
            } else if (od.objectType == IdData.LICH) {
                entitys.put(od.status.objectId, od);
            } else if (od.objectType == IdData.LICH_KILLABLE) {
                entitys.put(od.status.objectId, od);
                HeroLocations h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y);
                markActive(h);
            } else if (od.objectType == IdData.GHOST_INVON) {
                entitys.put(od.status.objectId, od);
            } else if (od.objectType == IdData.GHOST_KILLABLE) {
                entitys.put(od.status.objectId, od);
                HeroLocations h = findClosestHero((int) od.status.pos.x, (int) od.status.pos.y);
                markActive(h);
            } else if (od.objectType == IdData.CYCLOPS) { // cyclops
                entitys.put(od.status.objectId, od);
            } else if (od.objectType == IdData.OASIS_GIANT) { // Oasis Giant
                entitys.put(od.status.objectId, od);
            } else if (od.objectType == IdData.PHENIX) { // Phoenix Lord / 1729 Phoenix Reborn
                entitys.put(od.status.objectId, od);
            } else if (od.objectType == IdData.DEMON) { // Phoenix Lord / 1729 Phoenix Reborn
                entitys.put(od.status.objectId, od);
            }
//            if (!hashTester.containsKey(od.objectType)) {
//                hashTester.put(od.objectType, od);
//
//                if (od.status.stats.length == 4) {
//                    System.out.println(od);
//                    System.out.println(od.status.stats.length);
//                }
//            }
        }
        if (wallAdded && isGhostLoc()) {
            HeroLocations h = findClosestHero(model.getIntPlayerX(), model.getIntPlayerY(), nearHeroes);
            if (h.dist < 1000) {
                h.setType(HeroType.GHOST);
                markVisited(h);
                nearHeroes.remove(h);
            }
        }

    }

//    HashMap<Integer, ObjectData> hashTester = new HashMap<>();

    private boolean isCyclopsLoc(int x, int y) {
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j < 2; j++) {
                if (i == 0 && j == 0) continue;
                int t = mapTiles[x + i][y + j];
                if (t == IdData.WATER_DEEP_TILE) return true;
            }
        }
        return false;
    }

    private boolean isGhostLoc() {
        int directionCheck = 0;
        HashSet<Float> xFound = new HashSet<>();
        HashSet<Float> yFound = new HashSet<>();
        for (ObjectData o : entitys.values()) {
            if (o.objectType == IdData.DESTRUCTIBLE_GRAY_WALL || o.objectType == IdData.GRAY_WALL) {
                float wallX = o.status.pos.x;
                float wallY = o.status.pos.y;
                if (waterCheck((int) wallX, (int) wallY)) {
                    if (!xFound.contains(wallX) && !yFound.contains(wallY)) {
                        xFound.add(wallX);
                        yFound.add(wallY);
                        directionCheck++;
                    }
                    for (ObjectData o2 : entitys.values()) {
                        if (o.status.objectId == o2.status.objectId) continue;
                        if (o2.objectType == IdData.DESTRUCTIBLE_GRAY_WALL || o2.objectType == IdData.GRAY_WALL) {
                            float wallX2 = o2.status.pos.x;
                            float wallY2 = o2.status.pos.y;
                            if (waterCheck((int) wallX2, (int) wallY2)) {
                                if (!xFound.contains(wallX) && !yFound.contains(wallY)) {
                                    xFound.add(wallX);
                                    yFound.add(wallY);
                                    directionCheck++;
                                }
                            }
                        }

                        if (directionCheck >= 4) {
                            return true;
                        }
                    }
                }
            }
        }
        return directionCheck >= 4;
    }

    private boolean waterCheck(int x, int y) {
        for (int i = -5; i <= 5; i++) {
            for (int j = -5; j < 5; j++) {
                if (i == 0 && j == 0) continue;
                int t = mapTiles[x + i][y + j];
                if (t == IdData.WATER_DEEP_TILE || t == IdData.WATER_SHALLOW_TILE) return false;
            }
        }
        return true;
    }

    public void newTickUpdates(ObjectStatusData[] status) {
        if (!model.inRealm()) return;

        for (ObjectStatusData osd : status) {
            ObjectData found = entitys.get(osd.objectId);
            if (found != null) {
                for (int j = 0; j < osd.stats.length; j++) {
//                    System.out.println(osd.stats[j]);
                    if (osd.stats[j].statTypeNum == 1 && osd.stats[j].statValue == 0) {
                        HeroLocations h = findClosestHero((int) osd.pos.x, (int) osd.pos.y);
                        if (h.matchType(found)) {
                            markDead(h);
                        }
                    } else if (osd.stats[j].statTypeNum == 29 && (osd.stats[j].statValue & 16777216) > 0) {
                        HeroLocations h = findClosestHero((int) osd.pos.x, (int) osd.pos.y);
                        if (h.matchType(found)) {
                            markActive(h);
                        }
                    }
                }
            }
        }
    }

    public void markVisited(HeroLocations h) {
        if (h.setState(HeroState.MARK_VISITED)) {
            model.uploadSingleHero(h);
        }
    }

    public void markActive(HeroLocations h) {
        if (h.setState(HeroState.MARK_ACTIVE)) {
            model.uploadSingleHero(h);
        }
    }

    public void markDead(HeroLocations h) {
        if (h.setState(HeroState.MARK_DEAD)) {
            model.uploadSingleHero(h);
        }
    }

    public void reset() {
        entitys.clear();
        for (int i = 0; i < 2048; i++) {
            for (int j = 0; j < 2048; j++) {
                mapTiles[i][j] = 0;
            }
        }
    }
}
