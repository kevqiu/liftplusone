package com.kq.liftplusone.helpers;

import com.kq.liftplusone.models.Equipment;

/**
 * Created by Kevin on 5/14/2017.
 */

public class EquipmentHelper {
    public static Equipment StringToEquipment(String eq) {
        switch (eq) {
            case "Barbell":
                return Equipment.Barbell;
            case "Free Weights":
                return Equipment.BodyWeight;
            case "Machine":
                return Equipment.Machine;
            case "Body Weight":
                return Equipment.BodyWeight;
            case "Other":
                return Equipment.Other;
            default:
                return null;
        }
    }
}
