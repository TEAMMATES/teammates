package teammates.common.util;

import java.util.ArrayList;
import java.util.List;

import teammates.ui.template.ElementTag;

/**
 * This is a helper class to hold nationality-related functions.
 */
public final class NationalityHelper {
    
    private static final List<String> NATIONALITIES = new ArrayList<>();

    /**
     * This list of nationalities was created for the dropdown list and contains
     * 198 nationalities and an 'Other' option.
     * reference : "https://mytaskhelper.com/forums/5-faq-s/topics/98-nationality-dropdown-list"
     */
    static {
        addToNationalitiesList("Afghan");
        addToNationalitiesList("Albanian");
        addToNationalitiesList("Algerian");
        addToNationalitiesList("American");
        addToNationalitiesList("Andorran");
        addToNationalitiesList("Angolan");
        addToNationalitiesList("Antiguans");
        addToNationalitiesList("Argentinean");
        addToNationalitiesList("Armenian");
        addToNationalitiesList("Australian");
        addToNationalitiesList("Austrian");
        addToNationalitiesList("Azerbaijani");
        addToNationalitiesList("Bahamian");
        addToNationalitiesList("Bahraini");
        addToNationalitiesList("Bangladeshi");
        addToNationalitiesList("Barbadian");
        addToNationalitiesList("Barbudans");
        addToNationalitiesList("Batswana");
        addToNationalitiesList("Belarusian");
        addToNationalitiesList("Belgian");
        addToNationalitiesList("Belizean");
        addToNationalitiesList("Beninese");
        addToNationalitiesList("Bhutanese");
        addToNationalitiesList("Bolivian");
        addToNationalitiesList("Bosnian");
        addToNationalitiesList("Brazilian");
        addToNationalitiesList("British");
        addToNationalitiesList("Bruneian");
        addToNationalitiesList("Bulgarian");
        addToNationalitiesList("Burkinabe");
        addToNationalitiesList("Burmese");
        addToNationalitiesList("Burundian");
        addToNationalitiesList("Cambodian");
        addToNationalitiesList("Cameroonian");
        addToNationalitiesList("Canadian");
        addToNationalitiesList("Cape Verdean");
        addToNationalitiesList("Central African");
        addToNationalitiesList("Chadian");
        addToNationalitiesList("Chilean");
        addToNationalitiesList("Chinese");
        addToNationalitiesList("Colombian");
        addToNationalitiesList("Comoran");
        addToNationalitiesList("Congolese");
        addToNationalitiesList("Congolese");
        addToNationalitiesList("Costa Rican");
        addToNationalitiesList("Croatian");
        addToNationalitiesList("Cuban");
        addToNationalitiesList("Cypriot");
        addToNationalitiesList("Czech");
        addToNationalitiesList("Danish");
        addToNationalitiesList("Djibouti");
        addToNationalitiesList("Dominican");
        addToNationalitiesList("Dutch");
        addToNationalitiesList("Dutchman");
        addToNationalitiesList("Dutchwoman");
        addToNationalitiesList("East Timorese");
        addToNationalitiesList("Ecuadorean");
        addToNationalitiesList("Egyptian");
        addToNationalitiesList("Emirian");
        addToNationalitiesList("Equatorial Guinean");
        addToNationalitiesList("Eritrean");
        addToNationalitiesList("Estonian");
        addToNationalitiesList("Ethiopian");
        addToNationalitiesList("Fijian");
        addToNationalitiesList("Filipino");
        addToNationalitiesList("Finnish");
        addToNationalitiesList("French");
        addToNationalitiesList("Gabonese");
        addToNationalitiesList("Gambian");
        addToNationalitiesList("Georgian");
        addToNationalitiesList("German");
        addToNationalitiesList("Ghanaian");
        addToNationalitiesList("Greek");
        addToNationalitiesList("Grenadian");
        addToNationalitiesList("Guatemalan");
        addToNationalitiesList("Guinea-Bissauan");
        addToNationalitiesList("Guinean");
        addToNationalitiesList("Guyanese");
        addToNationalitiesList("Haitian");
        addToNationalitiesList("Herzegovinian");
        addToNationalitiesList("Honduran");
        addToNationalitiesList("Hungarian");
        addToNationalitiesList("I-Kiribati");
        addToNationalitiesList("Icelander");
        addToNationalitiesList("Indian");
        addToNationalitiesList("Indonesian");
        addToNationalitiesList("Iranian");
        addToNationalitiesList("Iraqi");
        addToNationalitiesList("Irish");
        addToNationalitiesList("Irish");
        addToNationalitiesList("Israeli");
        addToNationalitiesList("Italian");
        addToNationalitiesList("Ivorian");
        addToNationalitiesList("Jamaican");
        addToNationalitiesList("Japanese");
        addToNationalitiesList("Jordanian");
        addToNationalitiesList("Kazakhstani");
        addToNationalitiesList("Kenyan");
        addToNationalitiesList("Kittian and Nevisian");
        addToNationalitiesList("Kuwaiti");
        addToNationalitiesList("Kyrgyz");
        addToNationalitiesList("Laotian");
        addToNationalitiesList("Latvian");
        addToNationalitiesList("Lebanese");
        addToNationalitiesList("Liberian");
        addToNationalitiesList("Libyan");
        addToNationalitiesList("Liechtensteiner");
        addToNationalitiesList("Lithuanian");
        addToNationalitiesList("Luxembourger");
        addToNationalitiesList("Macedonian");
        addToNationalitiesList("Malagasy");
        addToNationalitiesList("Malawian");
        addToNationalitiesList("Malaysian");
        addToNationalitiesList("Maldivan");
        addToNationalitiesList("Malian");
        addToNationalitiesList("Maltese");
        addToNationalitiesList("Marshallese");
        addToNationalitiesList("Mauritanian");
        addToNationalitiesList("Mauritian");
        addToNationalitiesList("Mexican");
        addToNationalitiesList("Micronesian");
        addToNationalitiesList("Moldovan");
        addToNationalitiesList("Monacan");
        addToNationalitiesList("Mongolian");
        addToNationalitiesList("Moroccan");
        addToNationalitiesList("Mosotho");
        addToNationalitiesList("Motswana");
        addToNationalitiesList("Mozambican");
        addToNationalitiesList("Namibian");
        addToNationalitiesList("Nauruan");
        addToNationalitiesList("Nepalese");
        addToNationalitiesList("Netherlander");
        addToNationalitiesList("New Zealander");
        addToNationalitiesList("Ni-Vanuatu");
        addToNationalitiesList("Nicaraguan");
        addToNationalitiesList("Nigerian");
        addToNationalitiesList("Nigerien");
        addToNationalitiesList("North Korean");
        addToNationalitiesList("Northern Irish");
        addToNationalitiesList("Norwegian");
        addToNationalitiesList("Omani");
        addToNationalitiesList("Pakistani");
        addToNationalitiesList("Palauan");
        addToNationalitiesList("Panamanian");
        addToNationalitiesList("Papua New Guinean");
        addToNationalitiesList("Paraguayan");
        addToNationalitiesList("Peruvian");
        addToNationalitiesList("Polish");
        addToNationalitiesList("Portuguese");
        addToNationalitiesList("Qatari");
        addToNationalitiesList("Romanian");
        addToNationalitiesList("Russian");
        addToNationalitiesList("Rwandan");
        addToNationalitiesList("Saint Lucian");
        addToNationalitiesList("Salvadoran");
        addToNationalitiesList("Samoan");
        addToNationalitiesList("San Marinese");
        addToNationalitiesList("Sao Tomean");
        addToNationalitiesList("Saudi");
        addToNationalitiesList("Scottish");
        addToNationalitiesList("Senegalese");
        addToNationalitiesList("Serbian");
        addToNationalitiesList("Seychellois");
        addToNationalitiesList("Sierra Leonean");
        addToNationalitiesList("Singaporean");
        addToNationalitiesList("Slovakian");
        addToNationalitiesList("Solomon Islander");
        addToNationalitiesList("Somali");
        addToNationalitiesList("South African");
        addToNationalitiesList("South Korean");
        addToNationalitiesList("Spanish");
        addToNationalitiesList("Sri Lankan");
        addToNationalitiesList("Sudanese");
        addToNationalitiesList("Surinamer");
        addToNationalitiesList("Swazi");
        addToNationalitiesList("Swedish");
        addToNationalitiesList("Swiss");
        addToNationalitiesList("Syrian");
        addToNationalitiesList("Taiwanese");
        addToNationalitiesList("Tajik");
        addToNationalitiesList("Tanzanian");
        addToNationalitiesList("Thai");
        addToNationalitiesList("Togolese");
        addToNationalitiesList("Tongan");
        addToNationalitiesList("Tobagonian");
        addToNationalitiesList("Tunisian");
        addToNationalitiesList("Turkish");
        addToNationalitiesList("Tuvaluan");
        addToNationalitiesList("Ugandan");
        addToNationalitiesList("Ukrainian");
        addToNationalitiesList("Uruguayan");
        addToNationalitiesList("Uzbekistani");
        addToNationalitiesList("Venezuelan");
        addToNationalitiesList("Vietnamese");
        addToNationalitiesList("Welsh");
        addToNationalitiesList("Yemenite");
        addToNationalitiesList("Zambian");
        addToNationalitiesList("Zimbabwean");
        addToNationalitiesList("Other");
    }
    
    /**
     * This prevents instantiation. This is necessary because this is a utility class.
     */
    private NationalityHelper() {
    }

    /**
     * Adds the nationalities to the NATIONALITIES list.
     */
    public static void addToNationalitiesList(String...nationalities) {
        for (String nationality : nationalities) {
            NATIONALITIES.add(nationality);
        }
    }

    /**
     * Returns with the nationalities list.
     */
    public static List<String> getNationalities() {
        return NATIONALITIES;
    }
    
    /**
     * Returns the nationalities as HTML code.
     */
    public static List<ElementTag> getNationalitiesAsElementTags(String existingNationality) {
        List<String> nationalities = getNationalities();
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();

        for (String nationality : nationalities) {
            ElementTag option = createOption(nationality, nationality,
                    isNationalityToBeSelected(existingNationality, nationality));
            result.add(option);
        }

        return result;
    }

    /**
     * Decides whether there is a selected nationality or not.
     * @param existingNationality   The selected nationality.
     * @param nationality           Nationality from the drop-down list.
     * @return                      True if the existingNationality and the nationality are equal.
     */
    private static boolean isNationalityToBeSelected(String existingNationality, String nationality) {
        boolean isEditingExistingNationality = existingNationality != null;
        if (isEditingExistingNationality) {
            return nationality.equals(existingNationality);
        }
        return false;
    }

    /**
     * Returns an element tag representing a HTML option
     */
    public static ElementTag createOption(String text, String value, boolean isSelected) {
        if (isSelected) {
            return new ElementTag(text, "value", value, "selected", null);
        }
        return new ElementTag(text, "value", value);
    }

}
