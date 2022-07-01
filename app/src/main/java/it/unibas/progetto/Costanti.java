package it.unibas.progetto;

import java.util.Locale;

public class Costanti {
    public static final double DA_GRADI_A_RADIANTI = Math.PI/180;
    public static final int MAST_ROCC_REQUEST_CODE = 55;
    //public static final int MASTER_CHOOSER_REQUEST_CODE = 0;
    public static final String NODE_CONFIGURATION = "NODE_CONFIGURATION";
    public static final String NODE_MAIN_EXECUTOR = "NODE_MAIN_EXECUTOR";
    public static final String GRAPH_NAME="app/controllo_vocale";
    public static final String LAYER_GRIGLIA = "LayerGriglia";
    public static final String IP_SERVER_NTP = "193.204.114.232"; //ntp1.inrim.it	  193.204.114.232	  NTP (RFC 5905)
    public static final String TEMPI_PER_TIMER = "tempipertimer";
    //costanti velocità in modulo, quindi da considerarsi sia positive che negative
    public static final float DISTANZA_MINIMA_OSTACOLO = 0.25F;
    public static final double VELOCITA_LINEARE_MAX = 0.22;
    public static final double VELOCITA_ANGOLARE_MAX = 0.22;
    public static final double VELOCITA_LINEARE_RIDOTTA = 0.10; //0.06
    public static final double VELOCITA_ANGOLARE_RIDOTTA = 0.03;
    public static final int TENTATIVI_OBBIETTIVO = 3;
    //costanti-paremetri amcl
    //nome parametro e valore(soglia) per il quale verrà pubblicato un nuovo messaggio dal topic amcl_pose
    /*                       !!!!!!!!!
    attenzione! c'é un bug in amcl,
    -nel caso in cui siano stati settati i parametri amcl update_min_a e update_min a circa 0
    -nel caso di velocità lineari comprese tra -0.06 e 0,
    ===> la posizione del robot sarà sbagliata e si perderà!!
    ** aggiornamento **
    anche velocità 0 > vel > -0.1, non so quale sia la ragione del bug
    */
    public static final double PRECISIONE_LINEARE_M = 0.05; //0.07                                        //precisione di 0.05m = 5 centimetri (approssimata) dei valori dati da /move_base
    public static final double PRECISIONE_LINEARE_RIDUZIONE_M = 0.2;                                //precisione 20cm
    public static final double PRECISIONE_ANGOLARE_RAD = 1*DA_GRADI_A_RADIANTI;                             //gradi a radianti-> precisione di +-(valore)° rispetto all'angolo voluto
    public static final double PRECISIONE_ANGOLARE_CT_RAD = 2*DA_GRADI_A_RADIANTI;
    public static final double PRECISIONE_ANGOLARE_PER_RIDUZIONE_RAD = 8.0*DA_GRADI_A_RADIANTI;

    //nome nodi, nome topic, nome frame, nome service
    public static final String NOME_NODO_VISTA_MAPPA = "nodo_vista_mappa";
    public static final String NOME_NODO_COMANDO_VOCALE = "nodo_comando_vocale";
    public static final String NOME_NODO_JOYSTICK = "";
    public static final String ROBOT_FRAME = "base_link";
    public static final String NOME_TOPIC_ODOMETRIA = "/odom";
    public static final String NOME_TOPIC_OCCUPACITY_GRID = "map";
    public static final String NOME_TOPIC_LASER_SCAN = "scan";
    public static final String NOME_TOPIC_INITIAL_POSE = "initialpose";
    public static final String NOME_TOPIC_VELOCITA = "cmd_vel";
    public static final String NOME_TOPIC_POSIZIONE = "move_base_simple/goal";
    public static final String NOME_TOPIC_PERCORSO = "move_base/NavfnROS/plan";
    public static final String NOME_TOPIC_AMCL_POSE = "amcl_pose";
    public static final String NOME_SERVICE_PARAMETERS_AMCL = "/amcl/set_parameters";
    public static final String NOME_GROUP_PARAMETER= "default";
    public static final String NOME_TOPIC_OBBIETTIVO_CORRENTE = "/move_base/current_goal";
    public static final String NOME_TOPIC_CANCELLA_POSIZIONE = "/move_base/cancel";
    public static final String NOME_TOPIC_PARAMETER_UPDATE = "amcl/parameter_updates";
    public static final String NOME_FILE_TTF_ASSETS = "Roboto-Italic.ttf";
    public static final String NOME_PARAMETRO_AMCL_AG_POSE_MIN_VEL = "update_min_d";
    public static final String NOME_PARAMETRO_AMCL_AG_POSE_MIN_ANG = "update_min_a";
    public static final String IT = Locale.ITALIAN.getLanguage();
    public static final String EN = Locale.ENGLISH.getLanguage();
    public static final String FR = Locale.FRENCH.getLanguage();
    public static final String ES = new Locale("es", "ES").getLanguage();
    //risultati
    public static final String DATI_PERMANENTI = "datiPerm";
    public static final String COMANDO_INVIATO = "frase modificata";
    public static final String MOSTRA_GRIGLIA = "mostraGriglia";
    public static final String POSE_SALVATO = "poseSalvato";
}
