package id.fs.dia_padcv.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "beneficiaries")
data class Beneficiary(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_ben")
    val idBen: Long = 0,

    @ColumnInfo(name = "id_village_f")
    val villageIdF: Int,

    @ColumnInfo(name = "code_village_f")
    val codeVillageF: String = "",

    @ColumnInfo(name = "id_sproj_f")
    val idSprojF: Int = 100,

    @ColumnInfo(name = "ben_status")
    val benStatus: String = "Enable",

    @ColumnInfo(name = "sequences")
    val sequences: String? = null,

    @ColumnInfo(name = "id_profile_status_f")
    val profileStatusId: Int? = null,

    @ColumnInfo(name = "id_marital_status_f")
    val maritalStatusId: Int? = null,

    @ColumnInfo(name = "handicap")
    val handicap: String? = null,

    @ColumnInfo(name = "id_human_status_f")
    val humanStatusId: Int? = null,

    @ColumnInfo(name = "date_enregistrement")
    val dateEnregistrement: String,

    @ColumnInfo(name = "CODE_BENEF")
    val codeBenef: String,

    @ColumnInfo(name = "phone")
    val phone: String? = null,

    @ColumnInfo(name = "prenom_ben")
    val prenom: String? = null,

    @ColumnInfo(name = "nom_ben")
    val nom: String? = null,

    @ColumnInfo(name = "postnom_ben")
    val postnom: String? = null,

    @ColumnInfo(name = "age_ben")
    val age: Int = 0,

    @ColumnInfo(name = "Sexe")
    val sexe: String? = null,

    @ColumnInfo(name = "num_cart_ben")
    val numCarteBen: String? = null,

    @ColumnInfo(name = "chef_menage")
    val chefMenage: String = "OUI",

    @ColumnInfo(name = "enfant_chef_menage")
    val enfantChefMenage: String = "NON",

    @ColumnInfo(name = "repondant_present")
    val repondantPresent: String? = null,

    @ColumnInfo(name = "lien_repondant")
    val lienRepondant: String? = null,

    @ColumnInfo(name = "repondant_noms")
    val repondantNoms: String? = null,

    @ColumnInfo(name = "activity_status")
    val activityStatus: String? = null,

    @ColumnInfo(name = "taille_menage")
    val tailleMenage: Int = 0,

    @ColumnInfo(name = "enfants_6_17_ecole")
    val enfants6_17Ecole: Int = 0,

    @ColumnInfo(name = "handicapee_dans_menage")
    val handicapeDansMenage: String = "NON",

    @ColumnInfo(name = "personne_agee_dans_menage")
    val personneAgeeDansMenage: String = "NON",

    @ColumnInfo(name = "malade_chronique_dans_menage")
    val maladeChroniqueDansMenage: String = "NON",

    @ColumnInfo(name = "enfant_malnutri_dans_menage")
    val enfantMalnutriDansMenage: String = "NON",

    @ColumnInfo(name = "score_vulnerabilite_membre_menage")
    val scoreVulnerabiliteMembreMenage: Int = 0,

    @ColumnInfo(name = "source_revenus_principale")
    val sourceRevenusPrincipale: String? = null,

    @ColumnInfo(name = "autres_source_revenu_dans_menage")
    val autresSourceRevenuDansMenage: String? = null,

    @ColumnInfo(name = "habitation_menage")
    val habitationMenage: String = "OUI",

    @ColumnInfo(name = "toit")
    val toit: String? = null,

    @ColumnInfo(name = "mur")
    val mur: String? = null,

    @ColumnInfo(name = "sol")
    val sol: String? = null,

    @ColumnInfo(name = "nbre_pieces")
    val nbrePieces: Int = 0,

    @ColumnInfo(name = "nbre_prs_par_piece")
    val nbrePrsParPiece: Int = 0,

    @ColumnInfo(name = "proprietaire_parcelle")
    val proprietaireParcelle: String = "OUI",

    @ColumnInfo(name = "score_habitation")
    val scoreHabitation: Int = 0,

    @ColumnInfo(name = "acces_toilettes")
    val accesToilettes: String = "",

    @ColumnInfo(name = "distance_acces_toilettes")
    val distanceAccesToilettes: Int = 0,

    @ColumnInfo(name = "acces_eau_potable")
    val accesEauPotable: String = "",

    @ColumnInfo(name = "score_WASH")
    val scoreWASH: Int = 0,

    @ColumnInfo(name = "campagne_agricole_precedente")
    val campagneAgricolePrecedente: String = "OUI",

    @ColumnInfo(name = "produits_cultives")
    val produitsCultives: String = "",

    @ColumnInfo(name = "nbre_sacs_recoltes")
    val nbreSacsRecoltes: Int = 0,

    @ColumnInfo(name = "autres_infos")
    val autresInfos: String = "",

    @ColumnInfo(name = "nbre_champs")
    val nbreChamps: Int = 0,

    @ColumnInfo(name = "nbre_champs_agricoles")
    val nbreChampsAgricoles: Int = 0,

    @ColumnInfo(name = "nbre_maisons")
    val nbreMaisons: Int = 0,

    @ColumnInfo(name = "nbre_cases")
    val nbreCases: Int = 0,

    @ColumnInfo(name = "nbre_houes")
    val nbreHoues: Int = 0,

    @ColumnInfo(name = "nbre_charettes")
    val nbreCharettes: Int = 0,

    @ColumnInfo(name = "nbre_motos")
    val nbreMotos: Int = 0,

    @ColumnInfo(name = "nbre_velos")
    val nbreVelos: Int = 0,

    @ColumnInfo(name = "nbre_bovins")
    val nbreBovins: Int = 0,

    @ColumnInfo(name = "nbre_ovins")
    val nbreOvins: Int = 0,

    @ColumnInfo(name = "nbre_caprins")
    val nbreCaprins: Int = 0,

    @ColumnInfo(name = "nbre_volails")
    val nbreVolails: Int = 0,

    @ColumnInfo(name = "gros_elevage")
    val grosElevage: String = "NON",

    @ColumnInfo(name = "score_biens_menage")
    val scoreBiensMenage: Int = 0,

    @ColumnInfo(name = "nbre_repas_par_jour")
    val nbreRepasParJour: Int = 0,

    @ColumnInfo(name = "nbre_consommation_aliements_non_preferes_7jrs")
    val nbreConsommationAlimentsNonPreferes7jrs: Int = 0,

    @ColumnInfo(name = "aide_pour_manger_7jrs")
    val aidePourManger7jrs: String = "NON",

    @ColumnInfo(name = "emprunts_pour_manger_7jrs")
    val empruntsPourManger7jrs: Int = 0,

    @ColumnInfo(name = "diminuer_quantite_repas_7jrs")
    val diminuerQuantiteRepas7jrs: Int = 0,

    @ColumnInfo(name = "limiter_consommation_adultes_7jrs")
    val limiterConsommationAdultes7jrs: Int = 0,

    @ColumnInfo(name = "diminuer_nbre_repas_7jrs")
    val diminuerNbreRepas7jrs: Int = 0,

    @ColumnInfo(name = "nbre_utiliser_enfant_pour_manger")
    val nbreUtiliserEnfantPourManger: Int = 0,

    @ColumnInfo(name = "autres_infos_alimentation")
    val autresInfosAlimentation: String? = null,

    @ColumnInfo(name = "score_alimentation")
    val scoreAlimentation: Int = 0,

    @ColumnInfo(name = "commentaires_suggestions")
    val commentairesSuggestions: String? = null,

    @ColumnInfo(name = "num_ticket")
    val numTicket: String? = null,

    @ColumnInfo(name = "geopoint_ben")
    val geopointBen: String? = null,

    @ColumnInfo(name = "remark_ben")
    val remarkBen: String? = null,

    @ColumnInfo(name = "photo")
    val photo: String? = null,

    @ColumnInfo(name = "auteur")
    val auteur: String,

    @ColumnInfo(name = "qr_code")
    val qrCode: String? = null,

    @ColumnInfo(name = "score_statut_matrim")
    val scoreStatutMatrim: Int = 0,

    @ColumnInfo(name = "score_demographique")
    val scoreDemographique: Int = 0,

    @ColumnInfo(name = "score_final")
    val scoreFinal: Int = 0,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,

    @ColumnInfo(name = "latitude_ben")
    val latitude: String? = null,

    @ColumnInfo(name = "longitude_ben")
    val longitude: String? = null,

    @ColumnInfo(name = "altitude_ben")
    val altitude: String? = null,

    @ColumnInfo(name = "precision_ben")
    val precision: String? = null,
)