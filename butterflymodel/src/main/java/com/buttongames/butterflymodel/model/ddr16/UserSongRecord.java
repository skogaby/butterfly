package com.buttongames.butterflymodel.model.ddr16;

import com.buttongames.butterflymodel.model.ddr16.options.AppearanceOption;
import com.buttongames.butterflymodel.model.ddr16.options.ArrowColorOption;
import com.buttongames.butterflymodel.model.ddr16.options.ArrowSkinOption;
import com.buttongames.butterflymodel.model.ddr16.options.BoostOption;
import com.buttongames.butterflymodel.model.ddr16.options.CutOption;
import com.buttongames.butterflymodel.model.ddr16.options.FreezeArrowOption;
import com.buttongames.butterflymodel.model.ddr16.options.GuideLinesOption;
import com.buttongames.butterflymodel.model.ddr16.options.JudgementLayerOption;
import com.buttongames.butterflymodel.model.ddr16.options.JumpsOption;
import com.buttongames.butterflymodel.model.ddr16.options.LifeGaugeOption;
import com.buttongames.butterflymodel.model.ddr16.options.ScreenFilterOption;
import com.buttongames.butterflymodel.model.ddr16.options.ScrollOption;
import com.buttongames.butterflymodel.model.ddr16.options.SpeedOption;
import com.buttongames.butterflymodel.model.ddr16.options.StepZoneOption;
import com.buttongames.butterflymodel.model.ddr16.options.TurnOption;
import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

/**
 * Model class that represents a record for a song coming in from a client, so we can save
 * scores and ghost data.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_user_song_records")
public class UserSongRecord implements Externalizable {

    private static final long serialVersionUID = 1L;

    /** ID of the record, primary key */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /** The user this record belongs to */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile user;

    /** The PCBID of the machine this score was made on, for machine high scores */
    @Column(name = "machine_pcbid")
    private String machinePcbId;

    /** Which side the user played on */
    @Column(name = "play_side")
    private int playSide;

    /** Which style this record was for (singles, doubles, versus) */
    @Column(name = "play_style")
    private int playStyle;

    /** Which area the record is for */
    @Column(name = "area")
    private int area;

    /** The user's weight when they got the record */
    @Column(name = "weight_100")
    private int weight100;

    /** The name of the shop this record took place in */
    @Column(name = "shop_name")
    private String shopName;

    /** Whether the user was using premium play for this record */
    @Column(name = "is_premium")
    private boolean isPremium;

    /** Whether the user was using their eamusement pass */
    @Column(name = "is_ea_pass")
    private boolean isEaPass;

    /** Whether this record is a takeover (?) */
    @Column(name = "is_takeover")
    private boolean isTakeover;

    /** Whether this record is a repeater (?) */
    @Column(name = "is_repeater")
    private boolean isRepeater;

    /** Whether this record resulted in a game over */
    @Column(name = "is_game_over")
    private boolean isGameover;

    /** The location ID where this record took place */
    @Column(name = "location_id")
    private String locationId;

    /** The area of the shop where this record took place */
    @Column(name = "shop_area")
    private String shopArea;

    /** The stage number for the song */
    @Column(name = "stage_num")
    private int stageNum;

    /** The song ID for the song */
    @Column(name = "song_id")
    private int songId;

    /** The note type for the record (beginner, standard, expert, etc.) */
    @Column(name = "note_type")
    private int noteType;

    /** The grade for the record */
    @Column(name = "grade")
    private int grade;

    /** Which kind of flag/lamp the user got for their record */
    @Column(name = "clear_kind")
    private int clearKind;

    /** The score the user got for this record */
    @Column(name = "score")
    private int score;

    /** The user's EX score for this record */
    @Column(name = "ex_score")
    private int exScore;

    /** The user's max combo */
    @Column(name = "max_combo")
    private int maxCombo;

    /** The user's life at the end of the song */
    @Column(name = "life")
    private int life;

    /** How many notes were too fast */
    @Column(name = "fast_count")
    private int fastCount;

    /** How many notes were too slow */
    @Column(name = "slow_count")
    private int slowCount;

    /** How many notes were marvelous */
    @Column(name = "marvelous_count")
    private int marvelousCount;

    /** How many notes were perfect */
    @Column(name = "perfect_count")
    private int perfectCount;

    /** How many notes were great */
    @Column(name = "great_count")
    private int greatCount;

    /** How many notes were good */
    @Column(name = "good_count")
    private int goodCount;

    /** How many notes were boos */
    @Column(name = "boo_count")
    private int booCount;

    /** How many notes were misses */
    @Column(name = "miss_count")
    private int missCount;

    /** How many notes were OK */
    @Column(name = "ok_count")
    private int okCount;

    /** How many notes were NG (missed freezes) */
    @Column(name = "ng_count")
    private int ngCount;

    /** How many calories were burned */
    @Column(name = "calories")
    private int calories;

    /** The ghost data for the record */
    @OneToOne
    @JoinColumn(name = "ghost_data_id")
    private GhostData ghostData;

    /** The speed option for this record */
    @Column(name = "option_speed")
    @Enumerated(EnumType.STRING)
    private SpeedOption speedOption;

    /** The boost option for this record */
    @Column(name = "option_boost")
    @Enumerated(EnumType.STRING)
    private BoostOption boostOption;

    /** The appearance option for this record */
    @Column(name = "option_appearance")
    @Enumerated(EnumType.STRING)
    private AppearanceOption appearanceOption;

    /** The turn option for this record */
    @Column(name = "option_turn")
    @Enumerated(EnumType.STRING)
    private TurnOption turnOption;

    /** The step zone option for this record */
    @Column(name = "option_step_zone")
    @Enumerated(EnumType.STRING)
    private StepZoneOption stepZoneOption;

    /** The scroll option for this record */
    @Column(name = "option_scroll")
    @Enumerated(EnumType.STRING)
    private ScrollOption scrollOption;

    /** The arrow color option for this record */
    @Column(name = "option_arrow_color")
    @Enumerated(EnumType.STRING)
    private ArrowColorOption arrowColorOption;

    /** The cut option for this record */
    @Column(name = "option_cut")
    @Enumerated(EnumType.STRING)
    private CutOption cutOption;

    /** The freeze arrow option for this record */
    @Column(name = "option_freeze_arrow")
    @Enumerated(EnumType.STRING)
    private FreezeArrowOption freezeArrowOption;

    /** The jumps option for this record */
    @Column(name = "option_jumps")
    @Enumerated(EnumType.STRING)
    private JumpsOption jumpsOption;

    /** The arrow skin option for this record */
    @Column(name = "option_arrow_skin")
    @Enumerated(EnumType.STRING)
    private ArrowSkinOption arrowSkinOption;

    /** The screen filter option for this record */
    @Column(name = "option_screen_filter")
    @Enumerated(EnumType.STRING)
    private ScreenFilterOption screenFilterOption;

    /** The guidelines option for this record */
    @Column(name = "option_guidelines")
    @Enumerated(EnumType.STRING)
    private GuideLinesOption guideLinesOption;

    /** The life gauge option for this record */
    @Column(name = "option_life_gauge")
    @Enumerated(EnumType.STRING)
    private LifeGaugeOption lifeGaugeOption;

    /** The judgement layer option for this record */
    @Column(name = "option_judegement_layer")
    @Enumerated(EnumType.STRING)
    private JudgementLayerOption judgementLayerOption;

    /** Whether the fast/slow count was shown for this record */
    @Column(name = "option_fast_slow_count")
    private boolean showFastSlow;

    /** The base name of the song played */
    @Column(name = "song_base_name")
    private String songBaseName;

    /** The title of the song played */
    @Column(name = "song_title")
    private String songTitle;

    /** The artist of the song played */
    @Column(name = "song_artist")
    private String songArtist;

    /** The min BPM of the song */
    @Column(name = "bpm_min")
    private int bpmMin;

    /** The max BPM of the song */
    @Column(name = "bpm_max")
    private int bpmMax;

    /** The level of the song */
    @Column(name = "level")
    private int level;

    /** Which series the song belongs to */
    @Column(name = "series")
    private int series;

    /** Bemani flag (?) */
    @Column(name = "bemani_flag")
    private int bemaniFlag;

    /** Which genre the song is */
    @Column(name = "genre_flag")
    private int genreFlag;

    /** The song's limited (?) */
    @Column(name = "limited")
    private int limited;

    /** The song's region */
    @Column(name = "region")
    private int region;

    /** The groove radar voltage value */
    @Column(name = "gr_voltage")
    private int grVoltage;

    /** The groove radar stream value */
    @Column(name = "gr_stream")
    private int grStream;

    /** The groove radar chaos value */
    @Column(name = "gr_chaos")
    private int grChaos;

    /** The groove radar freeze value */
    @Column(name = "gr_freeze")
    private int grFreeze;

    /** The groove radar air value */
    @Column(name = "gr_air")
    private int grAir;

    /** The share (?) value */
    @Column(name = "share")
    private boolean share;

    /** When this record was concluded */
    @Column(name = "end_time")
    private LocalDateTime endtime;

    /** Which folder the record is for (?) */
    @Column(name = "folder")
    private int folder;

    public UserSongRecord() { }

    public UserSongRecord(UserProfile user, String machinePcbId, int playSide, int playStyle, int area, int weight100, String shopName,
                          boolean isPremium, boolean isEaPass, boolean isTakeover, boolean isRepeater, boolean isGameover,
                          String locationId, String shopArea, int stageNum, int songId, int noteType, int grade,
                          int clearKind, int score, int exScore, int maxCombo, int life, int fastCount, int slowCount,
                          int marvelousCount, int perfectCount, int greatCount, int goodCount, int booCount,
                          int missCount, int okCount, int ngCount, int calories, GhostData ghostData, SpeedOption speedOption,
                          BoostOption boostOption, AppearanceOption appearanceOption, TurnOption turnOption,
                          StepZoneOption stepZoneOption, ScrollOption scrollOption, ArrowColorOption arrowColorOption,
                          CutOption cutOption, FreezeArrowOption freezeArrowOption, JumpsOption jumpsOption,
                          ArrowSkinOption arrowSkinOption, ScreenFilterOption screenFilterOption, GuideLinesOption guideLinesOption,
                          LifeGaugeOption lifeGaugeOption, JudgementLayerOption judgementLayerOption, boolean showFastSlow,
                          String songBaseName, String songTitle, String songArtist, int bpmMin, int bpmMax, int level,
                          int series, int bemaniFlag, int genreFlag, int limited, int region, int grVoltage, int grStream,
                          int grChaos, int grFreeze, int grAir, boolean share, LocalDateTime endtime, int folder) {
        this.user = user;
        this.machinePcbId = machinePcbId;
        this.playSide = playSide;
        this.playStyle = playStyle;
        this.area = area;
        this.weight100 = weight100;
        this.shopName = shopName;
        this.isPremium = isPremium;
        this.isEaPass = isEaPass;
        this.isTakeover = isTakeover;
        this.isRepeater = isRepeater;
        this.isGameover = isGameover;
        this.locationId = locationId;
        this.shopArea = shopArea;
        this.stageNum = stageNum;
        this.songId = songId;
        this.noteType = noteType;
        this.grade = grade;
        this.clearKind = clearKind;
        this.score = score;
        this.exScore = exScore;
        this.maxCombo = maxCombo;
        this.life = life;
        this.fastCount = fastCount;
        this.slowCount = slowCount;
        this.marvelousCount = marvelousCount;
        this.perfectCount = perfectCount;
        this.greatCount = greatCount;
        this.goodCount = goodCount;
        this.booCount = booCount;
        this.missCount = missCount;
        this.okCount = okCount;
        this.ngCount = ngCount;
        this.calories = calories;
        this.ghostData = ghostData;
        this.speedOption = speedOption;
        this.boostOption = boostOption;
        this.appearanceOption = appearanceOption;
        this.turnOption = turnOption;
        this.stepZoneOption = stepZoneOption;
        this.scrollOption = scrollOption;
        this.arrowColorOption = arrowColorOption;
        this.cutOption = cutOption;
        this.freezeArrowOption = freezeArrowOption;
        this.jumpsOption = jumpsOption;
        this.arrowSkinOption = arrowSkinOption;
        this.screenFilterOption = screenFilterOption;
        this.guideLinesOption = guideLinesOption;
        this.lifeGaugeOption = lifeGaugeOption;
        this.judgementLayerOption = judgementLayerOption;
        this.showFastSlow = showFastSlow;
        this.songBaseName = songBaseName;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.bpmMin = bpmMin;
        this.bpmMax = bpmMax;
        this.level = level;
        this.series = series;
        this.bemaniFlag = bemaniFlag;
        this.genreFlag = genreFlag;
        this.limited = limited;
        this.region = region;
        this.grVoltage = grVoltage;
        this.grStream = grStream;
        this.grChaos = grChaos;
        this.grFreeze = grFreeze;
        this.grAir = grAir;
        this.share = share;
        this.endtime = endtime;
        this.folder = folder;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeObject(this.user);
        out.writeUTF(this.machinePcbId);
        out.writeInt(this.playSide);
        out.writeInt(this.playStyle);
        out.writeInt(this.area);
        out.writeInt(this.weight100);
        out.writeUTF(this.shopName);
        out.writeBoolean(this.isPremium);
        out.writeBoolean(this.isEaPass);
        out.writeBoolean(this.isTakeover);
        out.writeBoolean(this.isRepeater);
        out.writeBoolean(this.isGameover);
        out.writeUTF(this.locationId);
        out.writeUTF(this.shopArea);
        out.writeInt(this.stageNum);
        out.writeInt(this.songId);
        out.writeInt(this.noteType);
        out.writeInt(this.grade);
        out.writeInt(this.clearKind);
        out.writeInt(this.score);
        out.writeInt(this.exScore);
        out.writeInt(this.maxCombo);
        out.writeInt(this.life);
        out.writeInt(this.fastCount);
        out.writeInt(this.slowCount);
        out.writeInt(this.marvelousCount);
        out.writeInt(this.perfectCount);
        out.writeInt(this.greatCount);
        out.writeInt(this.goodCount);
        out.writeInt(this.booCount);
        out.writeInt(this.missCount);
        out.writeInt(this.okCount);
        out.writeInt(this.ngCount);
        out.writeInt(this.calories);
        out.writeObject(this.ghostData);
        out.writeObject(this.speedOption);
        out.writeObject(this.boostOption);
        out.writeObject(this.appearanceOption);
        out.writeObject(this.turnOption);
        out.writeObject(this.stepZoneOption);
        out.writeObject(this.scrollOption);
        out.writeObject(this.arrowColorOption);
        out.writeObject(this.cutOption);
        out.writeObject(this.freezeArrowOption);
        out.writeObject(this.jumpsOption);
        out.writeObject(this.arrowSkinOption);
        out.writeObject(this.screenFilterOption);
        out.writeObject(this.guideLinesOption);
        out.writeObject(this.lifeGaugeOption);
        out.writeObject(this.judgementLayerOption);
        out.writeBoolean(this.showFastSlow);
        out.writeUTF(this.songBaseName);
        out.writeUTF(this.songTitle);
        out.writeUTF(this.songArtist);
        out.writeInt(this.bpmMin);
        out.writeInt(this.bpmMax);
        out.writeInt(this.level);
        out.writeInt(this.series);
        out.writeInt(this.bemaniFlag);
        out.writeInt(this.genreFlag);
        out.writeInt(this.limited);
        out.writeInt(this.region);
        out.writeInt(this.grVoltage);
        out.writeInt(this.grStream);
        out.writeInt(this.grChaos);
        out.writeInt(this.grFreeze);
        out.writeInt(this.grAir);
        out.writeBoolean(this.share);
        out.writeObject(this.endtime);
        out.writeInt(this.folder);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setUser((UserProfile) in.readObject());
        this.setMachinePcbId(in.readUTF());
        this.setPlaySide(in.readInt());
        this.setPlayStyle(in.readInt());
        this.setArea(in.readInt());
        this.setWeight100(in.readInt());
        this.setShopName(in.readUTF());
        this.setPremium(in.readBoolean());
        this.setEaPass(in.readBoolean());
        this.setTakeover(in.readBoolean());
        this.setRepeater(in.readBoolean());
        this.setGameover(in.readBoolean());
        this.setLocationId(in.readUTF());
        this.setShopArea(in.readUTF());
        this.setStageNum(in.readInt());
        this.setSongId(in.readInt());
        this.setNoteType(in.readInt());
        this.setGrade(in.readInt());
        this.setClearKind(in.readInt());
        this.setScore(in.readInt());
        this.setExScore(in.readInt());
        this.setMaxCombo(in.readInt());
        this.setLife(in.readInt());
        this.setFastCount(in.readInt());
        this.setSlowCount(in.readInt());
        this.setMarvelousCount(in.readInt());
        this.setPerfectCount(in.readInt());
        this.setGreatCount(in.readInt());
        this.setGoodCount(in.readInt());
        this.setBooCount(in.readInt());
        this.setMissCount(in.readInt());
        this.setOkCount(in.readInt());
        this.setNgCount(in.readInt());
        this.setCalories(in.readInt());
        this.setGhostData((GhostData) in.readObject());
        this.setSpeedOption((SpeedOption) in.readObject());
        this.setBoostOption((BoostOption) in.readObject());
        this.setAppearanceOption((AppearanceOption) in.readObject());
        this.setTurnOption((TurnOption) in.readObject());
        this.setStepZoneOption((StepZoneOption) in.readObject());
        this.setScrollOption((ScrollOption) in.readObject());
        this.setArrowColorOption((ArrowColorOption) in.readObject());
        this.setCutOption((CutOption) in.readObject());
        this.setFreezeArrowOption((FreezeArrowOption) in.readObject());
        this.setJumpsOption((JumpsOption) in.readObject());
        this.setArrowSkinOption((ArrowSkinOption) in.readObject());
        this.setScreenFilterOption((ScreenFilterOption) in.readObject());
        this.setGuideLinesOption((GuideLinesOption) in.readObject());
        this.setLifeGaugeOption((LifeGaugeOption) in.readObject());
        this.setJudgementLayerOption((JudgementLayerOption) in.readObject());
        this.setShowFastSlow(in.readBoolean());
        this.setSongBaseName(in.readUTF());
        this.setSongTitle(in.readUTF());
        this.setSongArtist(in.readUTF());
        this.setBpmMin(in.readInt());
        this.setBpmMax(in.readInt());
        this.setLevel(in.readInt());
        this.setSeries(in.readInt());
        this.setBemaniFlag(in.readInt());
        this.setGenreFlag(in.readInt());
        this.setLimited(in.readInt());
        this.setRegion(in.readInt());
        this.setGrVoltage(in.readInt());
        this.setGrStream(in.readInt());
        this.setGrChaos(in.readInt());
        this.setGrFreeze(in.readInt());
        this.setGrAir(in.readInt());
        this.setShare(in.readBoolean());
        this.setEndtime((LocalDateTime) in.readObject());
        this.setFolder(in.readInt());
    }

    @GraphQLQuery(name = "id")
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @GraphQLQuery(name = "user")
    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    @GraphQLIgnore
    public String getMachinePcbId() {
        return machinePcbId;
    }

    public void setMachinePcbId(String machinePcbId) {
        this.machinePcbId = machinePcbId;
    }

    @GraphQLQuery(name = "playSide")
    public int getPlaySide() {
        return playSide;
    }

    public void setPlaySide(int playSide) {
        this.playSide = playSide;
    }

    @GraphQLQuery(name = "playStyle")
    public int getPlayStyle() {
        return playStyle;
    }

    public void setPlayStyle(int playStyle) {
        this.playStyle = playStyle;
    }

    @GraphQLQuery(name = "area")
    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    @GraphQLIgnore
    public int getWeight100() {
        return weight100;
    }

    public void setWeight100(int weight100) {
        this.weight100 = weight100;
    }

    @GraphQLIgnore
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @GraphQLIgnore
    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    @GraphQLIgnore
    public boolean isEaPass() {
        return isEaPass;
    }

    public void setEaPass(boolean eaPass) {
        isEaPass = eaPass;
    }

    @GraphQLIgnore
    public boolean isTakeover() {
        return isTakeover;
    }

    public void setTakeover(boolean takeover) {
        isTakeover = takeover;
    }

    @GraphQLIgnore
    public boolean isRepeater() {
        return isRepeater;
    }

    public void setRepeater(boolean repeater) {
        isRepeater = repeater;
    }

    @GraphQLQuery(name = "isGameover")
    public boolean isGameover() {
        return isGameover;
    }

    public void setGameover(boolean gameover) {
        isGameover = gameover;
    }

    @GraphQLQuery(name = "locationId")
    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @GraphQLQuery(name = "shopArea")
    public String getShopArea() {
        return shopArea;
    }

    public void setShopArea(String shopArea) {
        this.shopArea = shopArea;
    }

    @GraphQLQuery(name = "stageNum")
    public int getStageNum() {
        return stageNum;
    }

    public void setStageNum(int stageNum) {
        this.stageNum = stageNum;
    }

    @GraphQLQuery(name = "songId")
    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    @GraphQLQuery(name = "noteType")
    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    @GraphQLQuery(name = "grade")
    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @GraphQLQuery(name = "clearKind")
    public int getClearKind() {
        return clearKind;
    }

    public void setClearKind(int clearKind) {
        this.clearKind = clearKind;
    }

    @GraphQLQuery(name = "score")
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @GraphQLQuery(name = "exScore")
    public int getExScore() {
        return exScore;
    }

    public void setExScore(int exScore) {
        this.exScore = exScore;
    }

    @GraphQLQuery(name = "maxCombo")
    public int getMaxCombo() {
        return maxCombo;
    }

    public void setMaxCombo(int maxCombo) {
        this.maxCombo = maxCombo;
    }

    @GraphQLQuery(name = "life")
    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    @GraphQLQuery(name = "fastCount")
    public int getFastCount() {
        return fastCount;
    }

    public void setFastCount(int fastCount) {
        this.fastCount = fastCount;
    }

    @GraphQLQuery(name = "slowCount")
    public int getSlowCount() {
        return slowCount;
    }

    public void setSlowCount(int slowCount) {
        this.slowCount = slowCount;
    }

    @GraphQLQuery(name = "marvelousCount")
    public int getMarvelousCount() {
        return marvelousCount;
    }

    public void setMarvelousCount(int marvelousCount) {
        this.marvelousCount = marvelousCount;
    }

    @GraphQLQuery(name = "perfectCount")
    public int getPerfectCount() {
        return perfectCount;
    }

    public void setPerfectCount(int perfectCount) {
        this.perfectCount = perfectCount;
    }

    @GraphQLQuery(name = "greatCount")
    public int getGreatCount() {
        return greatCount;
    }

    public void setGreatCount(int greatCount) {
        this.greatCount = greatCount;
    }

    @GraphQLQuery(name = "goodCount")
    public int getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    @GraphQLQuery(name = "booCount")
    public int getBooCount() {
        return booCount;
    }

    public void setBooCount(int booCount) {
        this.booCount = booCount;
    }

    @GraphQLQuery(name = "missCount")
    public int getMissCount() {
        return missCount;
    }

    public void setMissCount(int missCount) {
        this.missCount = missCount;
    }

    @GraphQLQuery(name = "okCount")
    public int getOkCount() {
        return okCount;
    }

    public void setOkCount(int okCount) {
        this.okCount = okCount;
    }

    @GraphQLQuery(name = "ngCount")
    public int getNgCount() {
        return ngCount;
    }

    public void setNgCount(int ngCount) {
        this.ngCount = ngCount;
    }

    @GraphQLQuery(name = "calories")
    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    @GraphQLQuery(name = "ghostData")
    public GhostData getGhostData() {
        return ghostData;
    }

    public void setGhostData(GhostData ghostData) {
        this.ghostData = ghostData;
    }

    @GraphQLQuery(name = "speedOption")
    public SpeedOption getSpeedOption() {
        return speedOption;
    }

    public void setSpeedOption(SpeedOption speedOption) {
        this.speedOption = speedOption;
    }

    @GraphQLQuery(name = "boostOption")
    public BoostOption getBoostOption() {
        return boostOption;
    }

    public void setBoostOption(BoostOption boostOption) {
        this.boostOption = boostOption;
    }

    @GraphQLQuery(name = "appearanceOption")
    public AppearanceOption getAppearanceOption() {
        return appearanceOption;
    }

    public void setAppearanceOption(AppearanceOption appearanceOption) {
        this.appearanceOption = appearanceOption;
    }

    @GraphQLQuery(name = "turnOption")
    public TurnOption getTurnOption() {
        return turnOption;
    }

    public void setTurnOption(TurnOption turnOption) {
        this.turnOption = turnOption;
    }

    @GraphQLQuery(name = "stepZoneOption")
    public StepZoneOption getStepZoneOption() {
        return stepZoneOption;
    }

    public void setStepZoneOption(StepZoneOption stepZoneOption) {
        this.stepZoneOption = stepZoneOption;
    }

    @GraphQLQuery(name = "scrollOption")
    public ScrollOption getScrollOption() {
        return scrollOption;
    }

    public void setScrollOption(ScrollOption scrollOption) {
        this.scrollOption = scrollOption;
    }

    @GraphQLQuery(name = "arrowColorOption")
    public ArrowColorOption getArrowColorOption() {
        return arrowColorOption;
    }

    public void setArrowColorOption(ArrowColorOption arrowColorOption) {
        this.arrowColorOption = arrowColorOption;
    }

    @GraphQLQuery(name = "cutOption")
    public CutOption getCutOption() {
        return cutOption;
    }

    public void setCutOption(CutOption cutOption) {
        this.cutOption = cutOption;
    }

    @GraphQLQuery(name = "freezeArrowOption")
    public FreezeArrowOption getFreezeArrowOption() {
        return freezeArrowOption;
    }

    public void setFreezeArrowOption(FreezeArrowOption freezeArrowOption) {
        this.freezeArrowOption = freezeArrowOption;
    }

    @GraphQLQuery(name = "jumpsOption")
    public JumpsOption getJumpsOption() {
        return jumpsOption;
    }

    public void setJumpsOption(JumpsOption jumpsOption) {
        this.jumpsOption = jumpsOption;
    }

    @GraphQLQuery(name = "arrowSkinOption")
    public ArrowSkinOption getArrowSkinOption() {
        return arrowSkinOption;
    }

    public void setArrowSkinOption(ArrowSkinOption arrowSkinOption) {
        this.arrowSkinOption = arrowSkinOption;
    }

    @GraphQLQuery(name = "screenFilterOption")
    public ScreenFilterOption getScreenFilterOption() {
        return screenFilterOption;
    }

    public void setScreenFilterOption(ScreenFilterOption screenFilterOption) {
        this.screenFilterOption = screenFilterOption;
    }

    @GraphQLQuery(name = "guideLinesOption")
    public GuideLinesOption getGuideLinesOption() {
        return guideLinesOption;
    }

    public void setGuideLinesOption(GuideLinesOption guideLinesOption) {
        this.guideLinesOption = guideLinesOption;
    }

    @GraphQLQuery(name = "lifeGaugeOption")
    public LifeGaugeOption getLifeGaugeOption() {
        return lifeGaugeOption;
    }

    public void setLifeGaugeOption(LifeGaugeOption lifeGaugeOption) {
        this.lifeGaugeOption = lifeGaugeOption;
    }

    @GraphQLQuery(name = "judgementLayerOption")
    public JudgementLayerOption getJudgementLayerOption() {
        return judgementLayerOption;
    }

    public void setJudgementLayerOption(JudgementLayerOption judgementLayerOption) {
        this.judgementLayerOption = judgementLayerOption;
    }

    @GraphQLQuery(name = "showFastSlow")
    public boolean isShowFastSlow() {
        return showFastSlow;
    }

    public void setShowFastSlow(boolean showFastSlow) {
        this.showFastSlow = showFastSlow;
    }

    @GraphQLQuery(name = "songBaseName")
    public String getSongBaseName() {
        return songBaseName;
    }

    public void setSongBaseName(String songBaseName) {
        this.songBaseName = songBaseName;
    }

    @GraphQLQuery(name = "songTitle")
    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    @GraphQLQuery(name = "songArtist")
    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    @GraphQLQuery(name = "bpmMin")
    public int getBpmMin() {
        return bpmMin;
    }

    public void setBpmMin(int bpmMin) {
        this.bpmMin = bpmMin;
    }

    @GraphQLQuery(name = "bpmMax")
    public int getBpmMax() {
        return bpmMax;
    }

    public void setBpmMax(int bpmMax) {
        this.bpmMax = bpmMax;
    }

    @GraphQLQuery(name = "level")
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @GraphQLQuery(name = "series")
    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    @GraphQLQuery(name = "bemaniFlag")
    public int getBemaniFlag() {
        return bemaniFlag;
    }

    public void setBemaniFlag(int bemaniFlag) {
        this.bemaniFlag = bemaniFlag;
    }

    @GraphQLQuery(name = "genreFlag")
    public int getGenreFlag() {
        return genreFlag;
    }

    public void setGenreFlag(int genreFlag) {
        this.genreFlag = genreFlag;
    }

    @GraphQLQuery(name = "limited")
    public int getLimited() {
        return limited;
    }

    public void setLimited(int limited) {
        this.limited = limited;
    }

    @GraphQLQuery(name = "region")
    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    @GraphQLQuery(name = "grVoltage")
    public int getGrVoltage() {
        return grVoltage;
    }

    public void setGrVoltage(int grVoltage) {
        this.grVoltage = grVoltage;
    }

    @GraphQLQuery(name = "grStream")
    public int getGrStream() {
        return grStream;
    }

    public void setGrStream(int grStream) {
        this.grStream = grStream;
    }

    @GraphQLQuery(name = "grChaos")
    public int getGrChaos() {
        return grChaos;
    }

    public void setGrChaos(int grChaos) {
        this.grChaos = grChaos;
    }

    @GraphQLQuery(name = "grFreeze")
    public int getGrFreeze() {
        return grFreeze;
    }

    public void setGrFreeze(int grFreeze) {
        this.grFreeze = grFreeze;
    }

    @GraphQLQuery(name = "grAir")
    public int getGrAir() {
        return grAir;
    }

    public void setGrAir(int grAir) {
        this.grAir = grAir;
    }

    @GraphQLQuery(name = "share")
    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    @GraphQLQuery(name = "endtime")
    public LocalDateTime getEndtime() {
        return endtime;
    }

    public void setEndtime(LocalDateTime endtime) {
        this.endtime = endtime;
    }

    @GraphQLQuery(name = "folder")
    public int getFolder() {
        return folder;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }
}
