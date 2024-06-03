package packets;

import packets.Packet.IPacket;
import packets.incoming.*;
import packets.incoming.ip.IpAddress;
import packets.incoming.pets.*;
import packets.outgoing.*;
import packets.outgoing.pets.*;

import java.util.ArrayList;
import java.util.HashMap;

import static packets.PacketType.Direction.Incoming;
import static packets.PacketType.Direction.Outgoing;

/**
 * Packet are matched with the packet index sent as a header of packets and returned.
 */
public enum PacketType { //ChristmasTree™   ⛧   <-crown
                           FAILURE(  0, Incoming, FailurePacket::new),
                           TELEPORT( 1, Outgoing, TeleportPacket::new),
            CLAIM_LOGIN_REWARD_MSG(  3, Outgoing, ClaimDailyRewardMessage::new),
                        DELETE_PET(  4, Incoming, DeletePetMessage::new),
                      REQUESTTRADE(  5, Outgoing, RequestTradePacket::new),
              QUEST_FETCH_RESPONSE(  6, Incoming, QuestFetchResponsePacket::new),
                         JOINGUILD(  7, Outgoing, JoinGuildPacket::new),
                              PING(  8, Incoming, PingPacket::new),
                        PLAYERTEXT(  9, Outgoing, PlayerTextPacket::new),
                           NEWTICK( 10, Incoming, NewTickPacket::new),
                        SHOWEFFECT( 11, Incoming, ShowEffectPacket::new),
                 SERVERPLAYERSHOOT( 12, Incoming, ServerPlayerShootPacket::new),
                           USEITEM( 13, Outgoing, UseItemPacket::new),
                     TRADEACCEPTED( 14, Incoming, TradeAcceptedPacket::new),
                       GUILDREMOVE( 15, Outgoing, GuildRemovePacket::new),
                 PETUPGRADEREQUEST( 16, Outgoing, PetUpgradeRequestPacket::new),
                              GOTO( 18, Incoming, GotoPacket::new),
                           INVDROP( 19, Outgoing, InvDropPacket::new),
                          OTHERHIT( 20, Outgoing, OtherHitPacket::new),
                        NAMERESULT( 21, Incoming, NameResultPacket::new),
                         BUYRESULT( 22, Incoming, BuyResultPacket::new),
                         HATCH_PET( 23, Incoming, HatchPetMessage::new),
         ACTIVE_PET_UPDATE_REQUEST( 24, Outgoing, ActivePetUpdateRequestPacket::new),
                          ENEMYHIT( 25, Outgoing, EnemyHitPacket::new),
                       GUILDRESULT( 26, Incoming, GuildResultPacket::new),
                   EDITACCOUNTLIST( 27, Outgoing, EditAccountListPacket::new),
                      TRADECHANGED( 28, Incoming, TradeChangedPacket::new),
                       PLAYERSHOOT( 30, Outgoing, PlayerShootPacket::new),
                              PONG( 31, Outgoing, PongPacket::new),
               PET_CHANGE_SKIN_MSG( 33, Outgoing, ChangePetSkinPacket::new),
                         TRADEDONE( 34, Incoming, TradeDonePacket::new),
                        ENEMYSHOOT( 35, Incoming, EnemyShootPacket::new),
                       ACCEPTTRADE( 36, Outgoing, AcceptTradePacket::new),
                   CHANGEGUILDRANK( 37, Outgoing, ChangeGuildRankPacket::new),
                         PLAYSOUND( 38, Incoming, PlaySoundPacket::new),
                      VERIFY_EMAIL( 39, Incoming, VerifyEmailPacket::new),
                         SQUAREHIT( 40, Outgoing, SquareHitPacket::new),
                       NEW_ABILITY( 41, Incoming, NewAbilityMessage::new),
                            UPDATE( 42, Incoming, UpdatePacket::new),
                              TEXT( 44, Incoming, TextPacket::new),
                         RECONNECT( 45, Incoming, ReconnectPacket::new),
                             DEATH( 46, Incoming, DeathPacket::new),
                         USEPORTAL( 47, Outgoing, UsePortalPacket::new),
                    QUEST_ROOM_MSG( 48, Outgoing, GoToQuestRoomPacket::new),
                         ALLYSHOOT( 49, Incoming, AllyShootPacket::new),
                            RESKIN( 51, Outgoing, ReskinPacket::new),
                RESET_DAILY_QUESTS( 52, Outgoing, ResetDailyQuestsPacket::new),
               PET_CHANGE_FORM_MSG( 53, Outgoing, ReskinPetPacket::new),
                           INVSWAP( 55, Outgoing, InvSwapPacket::new),
                       CHANGETRADE( 56, Outgoing, ChangeTradePacket::new),
                            CREATE( 57, Outgoing, CreatePacket::new),
                      QUEST_REDEEM( 58, Outgoing, QuestRedeemPacket::new),
                       CREATEGUILD( 59, Outgoing, CreateGuildPacket::new),
                      SETCONDITION( 60, Outgoing, SetConditionPacket::new),
                              LOAD( 61, Outgoing, LoadPacket::new),
                              MOVE( 62, Outgoing, MovePacket::new),
                 KEY_INFO_RESPONSE( 63, Incoming, KeyInfoResponsePacket::new),
                               AOE( 64, Incoming, AoePacket::new),
                           GOTOACK( 65, Outgoing, GotoAckPacket::new),
               GLOBAL_NOTIFICATION( 66, Incoming, GlobalNotificationPacket::new),
                      NOTIFICATION( 67, Incoming, NotificationPacket::new),
                        CLIENTSTAT( 69, Incoming, ClientStatPacket::new),
                             HELLO( 74, Outgoing, HelloPacket::new),
                            DAMAGE( 75, Incoming, DamagePacket::new),
                   ACTIVEPETUPDATE( 76, Incoming, ActivePetPacket::new),
                    INVITEDTOGUILD( 77, Incoming, InvitedToGuildPacket::new),
                     PETYARDUPDATE( 78, Incoming, PetYardUpdate::new),
                   PASSWORD_PROMPT( 79, Incoming, PasswordPromptPacket::new),
                         UPDATEACK( 81, Outgoing, UpdateAckPacket::new),
                        QUESTOBJID( 82, Incoming, QuestObjectIdPacket::new),
                               PIC( 83, Incoming, PicPacket::new),
               REALM_HERO_LEFT_MSG( 84, Incoming, RealmHeroesLeftPacket::new),
                               BUY( 85, Outgoing, BuyPacket::new),
                        TRADESTART( 86, Incoming, TradeStartPacket::new),
                        EVOLVE_PET( 87, Incoming, EvolvedPetMessage::new),
                    TRADEREQUESTED( 88, Incoming, TradeRequestedPacket::new),
                            AOEACK( 89, Outgoing, AoeAckPacket::new),
                         PLAYERHIT( 90, Outgoing, PlayerHitPacket::new),
                       CANCELTRADE( 91, Outgoing, CancelTradePacket::new),
                           MAPINFO( 92, Incoming, MapInfoPacket::new),
                  LOGIN_REWARD_MSG( 93, Incoming, ClaimDailyRewardResponse::new),
                  KEY_INFO_REQUEST( 94, Outgoing, KeyInfoRequestPacket::new),
                         INVRESULT( 95, Incoming, InvResultPacket::new),
             QUEST_REDEEM_RESPONSE( 96, Incoming, QuestRedeemResponsePacket::new),
                        CHOOSENAME( 97, Outgoing, ChooseNamePacket::new),
                   QUEST_FETCH_ASK( 98, Outgoing, QuestFetchAskPacket::new),
                       ACCOUNTLIST( 99, Incoming, AccountListPacket::new),
                    CREATE_SUCCESS(101, Incoming, CreateSuccessPacket::new),
                      CHECKCREDITS(102, Outgoing, CheckCreditsPacket::new),
                      GROUNDDAMAGE(103, Outgoing, GroundDamagePacket::new),
                       GUILDINVITE(104, Outgoing, GuildInvitePacket::new),
                            ESCAPE(105, Outgoing, EscapePacket::new),
                              FILE(106, Incoming, FilePacket::new),
                     RESKIN_UNLOCK(107, Incoming, ReskinUnlockPacket::new),
         NEW_CHARACTER_INFORMATION(108, Incoming, NewCharacterInfoPacket::new),
                UNLOCK_INFORMATION(109, Incoming, UnlockInformationPacket::new),
                 QUEUE_INFORMATION(112, Incoming, QueueInfoPacket::new),
                      QUEUE_CANCEL(113, Outgoing, QueueCancelPacket::new),
          EXALTATION_BONUS_CHANGED(114, Incoming, ExaltationUpdatePacket::new),
          REDEEM_EXALTATION_REWARD(115, Outgoing, RedeemExaltationRewardPacket::new),
                      VAULT_UPDATE(117, Incoming, VaultContentPacket::new),
                     FORGE_REQUEST(118, Outgoing, ForgeRequestPacket::new),
                      FORGE_RESULT(119, Incoming, ForgeResultPacket::new),
         FORGE_UNLOCKED_BLUEPRINTS(120, Incoming, ForgeUnlockedBlueprints::new),
                         SHOOT_ACK(121, Outgoing, ShootAckPacket::new),
                  CHANGE_ALLYSHOOT(122, Outgoing, ChangeAllyShootPacket::new),
          GET_PLAYERS_LIST_MESSAGE(123, Outgoing, GetPlayersListPacket::new),
          MODERATOR_ACTION_MESSAGE(124, Outgoing, ModeratorActionMessagePacket::new),
                CREEP_MOVE_MESSAGE(126, Outgoing, CreepMovePacket::new),
                 CUSTOM_MAP_DELETE(129, Outgoing, CustomMapDeletePacket::new),
                   CUSTOM_MAP_LIST(131, Outgoing, CustomMapListPacket::new),
                         CREEP_HIT(133, Outgoing, CreepHitPacket::new),
                    PLAYER_CALLOUT(134, Outgoing, PlayerCalloutPacket::new),
                    BUY_REFINEMENT(136, Outgoing, BuyRefinementPacket::new),
                              DASH(137, Outgoing, DashPacket::new),
                          DASH_ACK(138, Outgoing, DashAckPacket::new),
                             STATS(139, Incoming, StatsPacket::new),
          BUY_CUSTOMISATION_SOCKET(140, Outgoing, BuyCustomisationSocketPacket::new),
                        FAVOUR_PET(145, Outgoing, FavourPetPacket::new),
                      SKIN_RECYCLE(146, Outgoing, SkinRecyclePacket::new),
                 CLAIM_BATTLE_PASS(149, Outgoing, ClaimBattlePassItemPacket::new),
         CLAIM_BP_MILESTONE_RESULT(150, Incoming, ClaimBPMilestoneResultPacket::new),
                BOOST_BP_MILESTONE(151, Outgoing, BoostBPMilestonePacket::new),
        CONVERT_SEASONAL_CHARACTER(154, Outgoing, ConvertSeasonalCharacterPacket::new),
                           RETITLE(155, Outgoing, RetitlePacket::new),
                   SET_GRAVE_STONE(156, Outgoing, SetGraveStonePacket::new),
                       SET_ABILITY(157, Outgoing, SetAbilityPacket::new),
                             EMOTE(159, Outgoing, EmotePacket::new),
                         BUY_EMOTE(160, Outgoing, BuyEmotePacket::new),
                SET_TRACKED_SEASON(162, Outgoing, SetTrackedSeasonPacket::new),
                     CLAIM_MISSION(163, Outgoing, ClaimMissionPacket::new),
                            STASIS(166, Incoming, StasisPacket::new),
                  SET_DISCOVERABLE(167, Outgoing, SetDiscoverablePacket::new),
                REALM_SCORE_UPDATE(169, Incoming, RealmScoreUpdatePacket::new),
         CLAIM_REWARDS_INFO_PROMPT(170, Incoming, ClaimRewardsInfoPromptPacket::new),
                CLAIM_CHEST_REWARD(171, Incoming, ClaimChestRewardPacket::new),
               CHEST_REWARD_RESULT(172, Incoming, ChestRewardResultPacket::new),
           UNLOCK_ENCHANTMENT_SLOT(173, Outgoing, UnlockEnchantmentSlotPacket::new),
                UNLOCK_ENCHANTMENT(175, Outgoing, UnlockEnchantmentPacket::new),
                 APPLY_ENCHANTMENT(177, Outgoing, ApplyEnchantmentPacket::new),
                 ACTIVATE_CRUCIBLE(180, Outgoing, ActivateCruciblePacket::new),
                  CRUCIBLE_REQUEST(182, Outgoing, CrucibleRequestPacket::new),
                 CRUCIBLE_RESPONSE(183, Incoming, CrucibleResponsePacket::new),
                 UPGRADE_ENCHANTER(185, Outgoing, UpgradeEnchanterPacket::new),
               UPGRADE_ENCHANTMENT(187, Outgoing, UpgradeEnchantmentPacket::new),
           REROLL_ALL_ENCHANTMENTS(189, Outgoing, RerollAllEnchantmentsPacket::new),
    RESET_ENCHANTMENT_REROLL_COUNT(191, Outgoing, ResetEnchantmentRerollCountPacket::new),
              CREATE_PARTY_MESSAGE(200, Outgoing, CreatePartyMessagePacket::new),
               PARTY_ACTION_RESULT(204, Incoming, PartyActionResultPacket::new),
             PARTY_INVITE_RESPONSE(209, Outgoing, PartyInviteResponsePacket::new),
             INCOMING_PARTY_INVITE(208, Incoming, IncomingPartyInvitePacket::new),
                      PARTY_ACTION(207, Outgoing, PartyActionPacket::new),
        INCOMING_PARTY_MEMBER_INFO(210, Incoming, IncomingPartyMemberInfoPacket::new),
                PARTY_MEMBER_ADDED(212, Incoming, PartyMemberAddedPacket::new),
                PARTY_LIST_MESSAGE(214, Incoming, PartyListMessagePacket::new),
                PARTY_JOIN_REQUEST(215, Incoming, PartyJoinRequestPacket::new),
            PARTY_REQUEST_RESPONSE(217, Incoming, PartyRequestResponsePacket::new),
                     FOR_RECONNECT(218, Incoming, ForReconnectPacket::new),
                       IP_ADDRESS(1000, Incoming, IpAddress::new);


    private static final HashMap<Integer, PacketType> PACKET_TYPE = new HashMap<>();
    private static final HashMap<Integer, IPacket> PACKET_TYPE_FACTORY = new HashMap<>();
    private static final HashMap<Class, PacketType> PACKET_CLASS = new HashMap<>();

    static {
        try {
            for (PacketType o : PacketType.values()) {
                PACKET_TYPE.put(o.index, o);
                PACKET_TYPE_FACTORY.put(o.index, o.packet);
                PACKET_CLASS.put(o.packet.factory().getClass(), o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final int index;
    private final Direction dir;
    private final IPacket packet;

    PacketType(int i, Direction d, IPacket p) {
        index = i;
        dir = d;
        packet = p;
    }

    /**
     * Get the index of the packet
     *
     * @return Index of the enum.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the enum by index.
     *
     * @param index The index.
     * @return Enum by index.
     */
    public static PacketType byOrdinal(int index) {
        return PACKET_TYPE.get(index);
    }

    /**
     * Get the enum type by class.
     *
     * @param packet The packet to be returned the type of.
     * @return Enum type.
     */
    public static PacketType byClass(Packet packet) {
        return PACKET_CLASS.get(packet.getClass());
    }

    /**
     * Retrieves the packet type from the PACKET_TYPE list.
     *
     * @param type Index of the packet needing to be retrieved.
     * @return Interface IPacket of the class being retrieved.
     */
    public static IPacket getPacket(int type) {
        return PACKET_TYPE_FACTORY.get(type);
    }

    /**
     * Checks if packet type exists in the PACKET_TYPE list.
     *
     * @param type Index of the packet.
     * @return True if the packet exists in the list of packets in PACKET_TYPE.
     */
    public static boolean containsKey(int type) {
        return PACKET_TYPE_FACTORY.containsKey(type);
    }

    /**
     * Returns the class of the enum.
     *
     * @return Class of the enum.
     */
    public Class<? extends Packet> getPacketClass() {
        return packet.factory().getClass();
    }

    /**
     * Gets the list of packets based on direction.
     *
     * @param isIncoming Filter the packets by direction.
     * @return Packet index list by direction.
     */
    public static Integer[] getPacketTypeByDirection(boolean isIncoming) {
        ArrayList<Integer> list = new ArrayList<>();
        for (PacketType o : PacketType.values()) {
            if (isIncoming ? o.dir == Direction.Incoming : o.dir == Direction.Outgoing) {
                list.add(o.index);
            }
        }
        return list.toArray(new Integer[0]);
    }

    public enum Direction {
        Incoming, Outgoing
    }
}
