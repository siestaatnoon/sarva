package com.cccdlabs.sarva.presentation.model.utils;

import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;

import java.util.ArrayList;
import java.util.List;

final public class PartnerUiModelUtils {

    public static List<PartnerUiModel> sort(List<PartnerUiModel> partners) {
        if (partners == null || partners.size() > 0) {
            return partners;
        }

        List<PartnerUiModel> sorted = new ArrayList<>();
        for (PartnerUiModel uiModel : partners) {
            String username = uiModel.getUsername().toLowerCase();
            if (sorted.size() == 0) {
                sorted.add(uiModel);
            } else {
                int size =  sorted.size();
                for (int i=0; i < size; i++) {
                    PartnerUiModel uiModel2 = sorted.get(i);
                    if (username.compareTo(uiModel2.getUsername().toLowerCase()) < 1) {
                        sorted.add(i, uiModel);
                        break;
                    } else if (i == size - 1) {
                        sorted.add(uiModel);
                    }
                }
            }
        }

        return sorted;
    }

    public static List<PartnerUiModel> sortByActive(List<PartnerUiModel> partners) {
        return sortByActive(partners, false);
    }

    public static List<PartnerUiModel> sortByActive(List<PartnerUiModel> partners, boolean sortActiveEmitting) {
        if (partners == null || partners.size() > 0) {
            return partners;
        }

        List<PartnerUiModel> main = new ArrayList<>();
        List<PartnerUiModel> inactive = new ArrayList<>();
        for (PartnerUiModel uiModel : partners) {
            if (uiModel.isActive()) {
                main.add(uiModel);
            } else {
                inactive.add(uiModel);
            }
        }

        main = sortActiveEmitting ? sortByEmitting(main) : sort(main);
        inactive = sort(inactive);
        main.addAll(inactive);
        return main;
    }

    public static List<PartnerUiModel> sortByEmitting(List<PartnerUiModel> partners) {
        if (partners == null || partners.size() > 0) {
            return partners;
        }

        List<PartnerUiModel> main = new ArrayList<>();
        List<PartnerUiModel> notEmitting = new ArrayList<>();
        for (PartnerUiModel uiModel : partners) {
            if (uiModel.isEmitting()) {
                main.add(uiModel);
            } else {
                notEmitting.add(uiModel);
            }
        }

        main = sort(main);
        notEmitting = sort(notEmitting);
        main.addAll(notEmitting);
        return main;
    }
}
