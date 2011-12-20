package com.afforess.minecartmaniaautomations;

import java.lang.reflect.Constructor;

import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;
import com.afforess.minecartmaniacore.signs.Sign;
import com.afforess.minecartmaniacore.signs.SignAction;
import com.afforess.minecartmaniasigncommands.sign.GenericAction;

/**
 * Move all the fancy new signs to here to avoid interfering with SignCommands too much.
 * 
 * @author Rob
 * 
 */
public enum AutomationsSign {
    // -- SMARTFOREST --
    SmartForestSign(GenericAction.class, "SmartForest"),
    SmartForestOffSign(GenericAction.class, "No SForest", "SmartForest", null),
    
    // -- AUTOMELON --
    AutoMelonSign(GenericAction.class, "AutoMelon"),
    AutoMelonOffSign(GenericAction.class, "Melon Off", "AutoMelon", null),
    
    // -- DEFOLIATE --
    DefoliateSign(GenericAction.class, "Defoliate"),
    FoliageSign(GenericAction.class, "Defoliate Off", "Defoliate", null),
    
    // -- AUTOFERTILIZE --
    AutoFertilizeSign(GenericAction.class, "AutoFertilize"),
    AutoFertilizeOffSign(GenericAction.class, "Fertilize Off", "AutoFertilize", null),
    
    // -- AUTOMINE --
    AutoMineSign(AutoMineSignAction.class),
    AutoMineOffSign(GenericAction.class, "Mining Off", "AutoMine", null),
    
    // -- AUTOWART --
    AutoWartSign(GenericAction.class, "AutoWart"),
    AutoWartOffSign(GenericAction.class, "Wart Off", "AutoWart", null),
    
    // -- DEBUGGING -- 
    DebugSign(GenericAction.class, "MMDebug"),
    DebugOffSign(GenericAction.class, "Debug Off", "MMDebug", null),
    
    // -- PUMPKINS --
    AutoPumpkinSign(GenericAction.class, "AutoPumpkin"),
    AutoPumpkinOffSign(GenericAction.class, "Pumpkin Off", "AutoPumpkin", null),
    
    // -- STEMS --
    AutoStemsSign(GenericAction.class, "Stems Too"),
    AutoStemsOffSign(GenericAction.class, "No Stems", "Stems Too", null),
    
    ;
    /////////////////////////////////////////////////////////////////
    // The following was stolen from SignType
    /////////////////////////////////////////////////////////////////
    
    AutomationsSign(final Class<? extends SignAction> action) {
        this.action = action;
        setting = null;
        key = null;
        value = null;
    }
    
    AutomationsSign(final Class<? extends SignAction> action, final String setting) {
        this.action = action;
        this.setting = setting;
        key = null;
        value = null;
    }
    
    AutomationsSign(final Class<? extends SignAction> action, final String setting, final String key, final Object value) {
        this.action = action;
        this.setting = setting;
        this.key = key;
        this.value = value;
    }
    
    private final Class<? extends SignAction> action;
    private final String setting;
    private final String key;
    private final Object value;
    
    public Class<? extends SignAction> getSignClass() {
        return action;
    }
    
    public SignAction getSignAction(final Sign sign) {
        try {
            
            Constructor<? extends SignAction> constructor;
            SignAction action;
            if (setting == null) {
                constructor = this.action.getConstructor(Sign.class);
                action = constructor.newInstance(sign);
            } else if (key == null) {
                constructor = this.action.getConstructor(String.class);
                action = constructor.newInstance(setting);
            } else {
                constructor = this.action.getConstructor(String.class, String.class, Object.class);
                action = constructor.newInstance(setting, key, value);
            }
            return action;
        } catch (final Exception e) {
            MinecartManiaLogger.getInstance().severe("Failed to read sign!");
            MinecartManiaLogger.getInstance().severe("Sign was :" + action);
            e.printStackTrace();
        }
        return null;
    }
}
