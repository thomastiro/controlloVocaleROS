package it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue;

import it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.ComandiVocali;

public abstract class ALinguaParteComuneA implements ILingua {

    @Override
    final public String getRegexDoubleCompleto() {
        return "(("+getMeno()+"|-)( )?)?" + ComandiVocali.REGEX_DOUBLE_SOLO_POSITIVI;
    }

    @Override
    final public String getRegexMetri() {
        return " " + getRegexDoubleCompleto() + "( )?(" + getStringOrForRegex(getMetri()) + "|m) ";
    }

    @Override
    final public String getRegexSecondi() {
        return " " + getRegexDoubleCompleto() + "( )?(" + getStringOrForRegex(getSecondi()) + "|s) ";
    }

    @Override
    final public String getRegexGradi() {
        return " " + getRegexDoubleCompleto() + "( )?(" + getStringOrForRegex(getGradi()) + "|°) ";
    }

    @Override
    final public String getRegexX() {
        return " (" + getStringOrForRegex(getCoordinataX()) + ")( )?" + getRegexDoubleCompleto();
    }

    @Override
    final public String getRegexY() {
        return " (" + getStringOrForRegex(getCoordinataY()) + ")( )?" + getRegexDoubleCompleto();
    }

    @Override
    final public String getRegexW() {
        return " (" + getStringOrForRegex(getCoordinataW()) + ")( )?" + getRegexDoubleCompleto();
    }

    @Override
    final public String[] getChiaviVai() {
        return getChiaviDaRegex(getRegexVaiSalvata());
    }

    @Override
    final public String[] getChiaviSalva() {
        return getChiaviDaRegex(getRegexPosizioneDaSalvareIncompleto());
    }

    @Override
    final public String getRegexPosizioneDaSalvare() {
        return getRegexPosizioneDaSalvareIncompleto() + ComandiVocali.REGEX_FORMATO_NOME_SALVABILE;
    }

    @Override
    final public String getRegexVaiSalvata() {
        return getRegexVaiSalvataIncompleto() + ComandiVocali.REGEX_FORMATO_NOME_SALVABILE;
    }

    protected abstract String[] getMetri();

    protected abstract String[] getSecondi();

    protected abstract String[] getGradi();

    protected abstract String getRegexVaiSalvataIncompleto();

    private String[] getChiaviDaRegex(String regex){
        regex = regex.trim();
        if(regex.charAt(0) == '('){
            int chiusura = regex.indexOf(")");
            String subRegex = regex.substring(1, chiusura);
            return subRegex.split("\\|");
        } else {
            String[] chiavi = new String[1];
            chiavi[0] = regex.split(" ")[0];
            return chiavi;
        }
    }

    private String getStringOrForRegex(String[] arrayString){
        StringBuilder rexChiavi = new StringBuilder(arrayString[0]);
        for (int i = 1; i < arrayString.length; i++) {
            rexChiavi.append("|").append(arrayString[i]);
        }
        return rexChiavi.toString();
    }
}
