package com.buttongames.butterflymodel.model.ddr16;

import com.buttongames.butterflymodel.model.ButterflyUser;
import com.buttongames.butterflymodel.model.ddr16.options.AppearanceOption;
import com.buttongames.butterflymodel.model.ddr16.options.ArrowColorOption;
import com.buttongames.butterflymodel.model.ddr16.options.ArrowSkinOption;
import com.buttongames.butterflymodel.model.ddr16.options.BoostOption;
import com.buttongames.butterflymodel.model.ddr16.options.CutOption;
import com.buttongames.butterflymodel.model.ddr16.options.DancerOption;
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

/**
 * Model class that represents a user profile in DDR 16 (DDR A).
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_profiles")
public class UserProfile implements Externalizable {

    private static final long serialVersionUID = 1L;

    /** ID of the object, primary key */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /** The user this profile belongs to */
    @OneToOne
    @JoinColumn(name = "user_id")
    private ButterflyUser user;

    /** The name of this profile */
    @Column(name = "name")
    private String name;

    /** The dancer code for this profile */
    @Column(name = "dancer_code")
    private int dancerCode;

    /** The area for this profile */
    @Column(name = "area")
    private int area;

    /** Whether to display the weight */
    @Column(name = "should_display_weight")
    private boolean displayWeight;

    /** Extra charge for this profile */
    @Column(name = "extra_charge")
    private int extraCharge;

    /** Number of singles plays this profile has had */
    @Column(name = "singles_plays")
    private int singlesPlays;

    /** Number of doubles plays this profile has had */
    @Column(name = "doubles_plays")
    private int doublesPlays;

    /** Number of total plays this profile has had */
    @Column(name = "total_plays")
    private int totalPlays;

    /** The player's weight (in kilograms) */
    @Column(name = "weight")
    private double weight;

    /** The player's chosen dancer character */
    @Column(name = "dancer_character")
    @Enumerated(EnumType.STRING)
    private DancerOption character;

    /** The player's chosen speed option */
    @Column(name = "option_speed")
    @Enumerated(EnumType.STRING)
    private SpeedOption speedOption;

    /** The player's chosen boost option */
    @Column(name = "option_boost")
    @Enumerated(EnumType.STRING)
    private BoostOption boostOption;

    /** The player's chosen appearance option */
    @Column(name = "option_appearance")
    @Enumerated(EnumType.STRING)
    private AppearanceOption appearanceOption;

    /** The player's chosen turn option */
    @Column(name = "option_turn")
    @Enumerated(EnumType.STRING)
    private TurnOption turnOption;

    /** The player's chosen step zone option */
    @Column(name = "option_step_zone")
    @Enumerated(EnumType.STRING)
    private StepZoneOption stepZoneOption;

    /** The player's chosen scroll option */
    @Column(name = "option_scroll")
    @Enumerated(EnumType.STRING)
    private ScrollOption scrollOption;

    /** The player's chosen arrow color option */
    @Column(name = "option_arrow_color")
    @Enumerated(EnumType.STRING)
    private ArrowColorOption arrowColorOption;

    /** The player's chosen cut option */
    @Column(name = "option_cut")
    @Enumerated(EnumType.STRING)
    private CutOption cutOption;

    /** The player's chosen freeze arrow option */
    @Column(name = "option_freeze_arrow")
    @Enumerated(EnumType.STRING)
    private FreezeArrowOption freezeArrowOption;

    /** The player's chosen jump option */
    @Column(name = "option_jumps")
    @Enumerated(EnumType.STRING)
    private JumpsOption jumpsOption;

    /** The player's chosen arrow skin option */
    @Column(name = "option_arrow_skin")
    @Enumerated(EnumType.STRING)
    private ArrowSkinOption arrowSkinOption;

    /** The player's chosen screen filter option */
    @Column(name = "option_screen_filter")
    @Enumerated(EnumType.STRING)
    private ScreenFilterOption screenFilterOption;

    /** The player's chosen guidelines option */
    @Column(name = "option_guidelines")
    @Enumerated(EnumType.STRING)
    private GuideLinesOption guideLinesOption;

    /** The player's chosen life gauge option */
    @Column(name = "option_life_gauge")
    @Enumerated(EnumType.STRING)
    private LifeGaugeOption lifeGaugeOption;

    /** The player's chosen judgement layer option */
    @Column(name = "option_judgement_layer")
    @Enumerated(EnumType.STRING)
    private JudgementLayerOption judgementLayerOption;

    /** Whether or not to show the fast/slow results at the end of the game */
    @Column(name = "show_fast_slow_results")
    private boolean showFastSlow;

    /** Calories from the last day (?) */
    @Column(name = "last_calories")
    private int lastCalories;

    /** The player's first rival. */
    @ManyToOne
    @JoinColumn(name = "rival_1_id")
    private UserProfile rival1;

    /** The player's second rival. */
    @ManyToOne
    @JoinColumn(name = "rival_2_id")
    private UserProfile rival2;

    /** The player's third rival. */
    @ManyToOne
    @JoinColumn(name = "rival_3_id")
    private UserProfile rival3;

    /** The rest of the CSV values for the LAST column in the profile response that we don't know, but they're probably important to save */
    @Column(name = "unk_last", columnDefinition = "TEXT")
    private String unkLast;

    /** The user's Dan ranking for singles */
    @Column(name = "single_class")
    private Integer singleClass;

    /** The user's Dan ranking for doubles */
    @Column(name = "double_class")
    private Integer doubleClass;

    public UserProfile() { }

    public UserProfile(ButterflyUser user, String name, int dancerCode, int area, boolean displayWeight, int extraCharge,
                       int singlesPlays, int doublesPlays, int totalPlays, double weight, DancerOption character,
                       SpeedOption speedOption, BoostOption boostOption, AppearanceOption appearanceOption,
                       TurnOption turnOption, StepZoneOption stepZoneOption, ScrollOption scrollOption,
                       ArrowColorOption arrowColorOption, CutOption cutOption, FreezeArrowOption freezeArrowOption,
                       JumpsOption jumpsOption, ArrowSkinOption arrowSkinOption, ScreenFilterOption screenFilterOption,
                       GuideLinesOption guideLinesOption, LifeGaugeOption lifeGaugeOption, JudgementLayerOption judgementLayerOption,
                       boolean showFastSlow, int lastCalories, UserProfile rival1, UserProfile rival2, UserProfile rival3,
                       String unkLast, int singleClass, int doubleClass) {
        this.user = user;
        this.name = name;
        this.dancerCode = dancerCode;
        this.area = area;
        this.displayWeight = displayWeight;
        this.extraCharge = extraCharge;
        this.singlesPlays = singlesPlays;
        this.doublesPlays = doublesPlays;
        this.totalPlays = totalPlays;
        this.weight = weight;
        this.character = character;
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
        this.lastCalories = lastCalories;
        this.rival1 = rival1;
        this.rival2 = rival2;
        this.rival3 = rival3;
        this.unkLast = unkLast;
        this.singleClass = singleClass;
        this.doubleClass = doubleClass;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeObject(this.user);
        out.writeUTF(this.name);
        out.writeInt(this.dancerCode);
        out.writeInt(this.area);
        out.writeBoolean(this.displayWeight);
        out.writeInt(this.extraCharge);
        out.writeInt(this.singlesPlays);
        out.writeInt(this.doublesPlays);
        out.writeInt(this.totalPlays);
        out.writeDouble(this.weight);
        out.writeObject(this.character);
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
        out.writeInt(this.lastCalories);
        out.writeObject(this.rival1);
        out.writeObject(this.rival2);
        out.writeObject(this.rival3);
        out.writeUTF(this.unkLast);
        out.writeInt(this.singleClass);
        out.writeInt(this.doubleClass);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setUser((ButterflyUser) in.readObject());
        this.setName(in.readUTF());
        this.setDancerCode(in.readInt());
        this.setArea(in.readInt());
        this.setDisplayWeight(in.readBoolean());
        this.setExtraCharge(in.readInt());
        this.setSinglesPlays(in.readInt());
        this.setDoublesPlays(in.readInt());
        this.setTotalPlays(in.readInt());
        this.setWeight(in.readDouble());
        this.setCharacter((DancerOption) in.readObject());
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
        this.setLastCalories(in.readInt());
        this.setRival1((UserProfile) in.readObject());
        this.setRival2((UserProfile) in.readObject());
        this.setRival3((UserProfile) in.readObject());
        this.setUnkLast(in.readUTF());
        this.setSingleClass(in.readInt());
        this.setDoubleClass(in.readInt());
    }

    @GraphQLQuery(name = "id")
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @GraphQLQuery(name = "user")
    public ButterflyUser getUser() {
        return user;
    }

    public void setUser(ButterflyUser user) {
        this.user = user;
    }

    @GraphQLQuery(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @GraphQLQuery(name = "dancerCode")
    public int getDancerCode() {
        return dancerCode;
    }

    public void setDancerCode(int dancerCode) {
        this.dancerCode = dancerCode;
    }

    @GraphQLQuery(name = "area")
    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    @GraphQLIgnore
    public boolean isDisplayWeight() {
        return displayWeight;
    }

    public void setDisplayWeight(boolean displayWeight) {
        this.displayWeight = displayWeight;
    }

    @GraphQLIgnore
    public int getExtraCharge() {
        return extraCharge;
    }

    public void setExtraCharge(int extraCharge) {
        this.extraCharge = extraCharge;
    }

    @GraphQLQuery(name = "singlesPlays")
    public int getSinglesPlays() {
        return singlesPlays;
    }

    public void setSinglesPlays(int singlesPlays) {
        this.singlesPlays = singlesPlays;
    }

    @GraphQLQuery(name = "doublesPlays")
    public int getDoublesPlays() {
        return doublesPlays;
    }

    public void setDoublesPlays(int doublesPlays) {
        this.doublesPlays = doublesPlays;
    }

    @GraphQLQuery(name = "totalPlays")
    public int getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(int totalPlays) {
        this.totalPlays = totalPlays;
    }

    @GraphQLIgnore
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @GraphQLQuery(name = "character")
    public DancerOption getCharacter() {
        return character;
    }

    public void setCharacter(DancerOption character) {
        this.character = character;
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

    @GraphQLIgnore
    public int getLastCalories() {
        return lastCalories;
    }

    public void setLastCalories(int lastCalories) {
        this.lastCalories = lastCalories;
    }

    @GraphQLQuery(name = "rival1")
    public UserProfile getRival1() {
        return rival1;
    }

    public void setRival1(UserProfile rival1) {
        this.rival1 = rival1;
    }

    @GraphQLQuery(name = "rival2")
    public UserProfile getRival2() {
        return rival2;
    }

    public void setRival2(UserProfile rival2) {
        this.rival2 = rival2;
    }

    @GraphQLQuery(name = "rival3")
    public UserProfile getRival3() {
        return rival3;
    }

    public void setRival3(UserProfile rival3) {
        this.rival3 = rival3;
    }

    @GraphQLIgnore
    public String getUnkLast() {
        return unkLast;
    }

    public void setUnkLast(String unkLast) {
        this.unkLast = unkLast;
    }

    @GraphQLQuery(name = "singleClass")
    public int getSingleClass() {
        return (this.singleClass == null) ? 0 : singleClass;
    }

    public void setSingleClass(int singleClass) {
        this.singleClass = singleClass;
    }

    @GraphQLQuery(name = "doubleClass")
    public int getDoubleClass() {
        return (this.doubleClass == null) ? 0 : doubleClass;
    }

    public void setDoubleClass(int doubleClass) {
        this.doubleClass = doubleClass;
    }
}
