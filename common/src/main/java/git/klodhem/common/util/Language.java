package git.klodhem.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    DE_DE("de-DE"),
    EN_US("en-US"),
    ES_ES("es-ES"),
    FI_FI("fi-FI"),
    FR_FR("fr-FR"),
    HE_HE("he-HE"),
    IT_IT("it-IT"),
    KK_KZ("kk-KZ"),
    NL_NL("nl-NL"),
    PL_PL("pl-PL"),
    PT_PT("pt-PT"),
    PT_BR("pt-BR"),
    RU_RU("ru-RU"),
    SV_SE("sv-SE"),
    TR_TR("tr-TR"),
    UZ_UZ("uz-UZ");

    private final String code;
}
