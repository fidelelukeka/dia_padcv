package id.fs.dia_padcv.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// ✅ Correspond exactement à la structure JSON de ton API
data class ApiResponse(
    val status: String,
    val message: String,
    val data: List<User>
)

data class User(
    val username: String,
    val password_hash: String,
    val role: String?
)

// ---------------- VILLAGE ----------------
data class Village(
    val id_village: Int,      // ⚡ Corrigé en Int pour Room
    val name_village: String   // ⚡ Correspond à l'entity Room
)

data class VillageResponse(
    val status: String,
    val message: String,
    val data: List<Village>
)

// ---------------- WAREHOUSE ----------------
data class Warehouse(
    val warehouse_id: String,
    val name_warehouse: String
)

// ✅ Data class pour l'envoi d'un entrepôt
data class WarehouseRequest(
    val id_village_f: Int? = null,         // ⚡ Int pour correspondre à villageIdF
    val latitude_wrhs: Double? = null,
    val longitude_wrhs: Double? = null,
    val altitude_wrhs: Double? = null,
    val precision_wrhs: Double? = null,
    val name_warehouse: String,
    val name_responsable: String? = null,
    val photo: String? = null,
    val type: String? = null
)

data class WarehouseResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

// ---------------- COLIS ----------------
data class ColisRequest(
    @SerializedName("QR_code") val qrCode: String,
    @SerializedName("produit_type") val produitType: String,
    @SerializedName("produit_contenu") val produitContenu: String,
    val quantity: Int,
    val unit: String,
    val quality: String,
    @SerializedName("warehouse_id_f") val warehouseIdF: Int,
    val status: String
)

data class ColisResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

// ---------------- BENEFICIARY ----------------
data class BeneficiaryRequest(
    @SerializedName("id_village_f") val villageIdF: Int = 1,
    @SerializedName("code_village_f") val codeVillageF: String = "1",
    @SerializedName("id_sproj_f") val idSprojF: Int = 1,
    @SerializedName("id_profile_status_f") val idProfileStatusF: Int?,
    @SerializedName("id_marital_status_f") val idMaritalStatusF: Int?,
    @SerializedName("handicap") val handicap: String?,
    @SerializedName("id_human_status_f") val idHumanStatusF: Int?,
    @SerializedName("date_enregistrement") val dateEnregistrement: String, // ISO 8601
    @SerializedName("CODE_BENEF") val codeBenef: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("prenom_ben") val prenom: String?,
    @SerializedName("nom_ben") val nom: String?,
    @SerializedName("postnom_ben") val postnom: String?,
    @SerializedName("age_ben") val age: String?, // varchar(255)
    @SerializedName("Sexe") val sexe: String?,
    @SerializedName("num_cart_ben") val numCarteBen: String?,
    @SerializedName("chef_menage") val chefMenage: String, // "OUI"/"NON"
    @SerializedName("enfant_chef_menage") val enfantChefMenage: String,
    @SerializedName("repondant_present") val repondantPresent: String?,
    @SerializedName("lien_repondant") val lienRepondant: String?,
    @SerializedName("repondant_noms") val repondantNoms: String?,
    @SerializedName("activity_status") val activityStatus: String?,
    @SerializedName("taille_menage") val tailleMenage: Int,
    @SerializedName("enfants_6_17_ecole") val enfants6_17Ecole: String?,
    @SerializedName("handicapee_dans_menage") val handicapDansMenage: String,
    @SerializedName("personne_agee_dans_menage") val personneAgeeDansMenage: String,
    @SerializedName("malade_chronique_dans_menage") val maladeChroniqueDansMenage: String,
    @SerializedName("enfant_malnutri_dans_menage") val enfantMalnutriDansMenage: String,
    @SerializedName("score_vulnerabilite_membre_menage") val scoreMembreMenage: Int,
    @SerializedName("source_revenus_principale") val sourceRevenusPrincipale: String?,
    @SerializedName("autres_source_revenu_dans_menage") val autresSourceRevenuDansMenage: Int,
    @SerializedName("habitation_menage") val habitationMenage: String,
    @SerializedName("toit") val toit: String?,
    @SerializedName("mur") val mur: String?,
    @SerializedName("sol") val sol: String?,
    @SerializedName("nbre_pieces") val nbrePieces: String,
    @SerializedName("nbre_prs_par_piece") val nbrePrsParPiece: String,
    @SerializedName("proprietaire_parcelle") val proprietaireParcelle: String,
    @SerializedName("score_habitation") val scoreHabitation: Int,
    @SerializedName("acces_toilettes") val accesToilettes: String,
    @SerializedName("distance_acces_toilettes") val distanceAccesToilettes: String,
    @SerializedName("acces_eau_potable") val accesEauPotable: String,
    @SerializedName("score_WASH") val scoreWash: Int,
    @SerializedName("campagne_agricole_precedente") val campagneAgricolePrecedente: String,
    @SerializedName("produits_cultives") val produitsCultives: String,
    @SerializedName("nbre_sacs_recoltes") val nbreSacsRecoltes: String,
    @SerializedName("autres_infos") val autresInfos: String?,
    @SerializedName("nbre_champs") val nbreChamps: Int,
    @SerializedName("nbre_champs_agricoles") val nbreChampsAgricoles: Int,
    @SerializedName("nbre_maisons") val nbreMaisons: Int,
    @SerializedName("nbre_cases") val nbreCases: Int,
    @SerializedName("nbre_houes") val nbreHoues: Int,
    @SerializedName("nbre_charettes") val nbreCharrettes: Int,
    @SerializedName("nbre_motos") val nbreMotos: Int,
    @SerializedName("nbre_velos") val nbreVelos: Int,
    @SerializedName("nbre_bovins") val nbreBovins: Int,
    @SerializedName("nbre_ovins") val nbreOvins: Int,
    @SerializedName("nbre_caprins") val nbreCaprins: Int,
    @SerializedName("nbre_volails") val nbreVolails: Int,
    @SerializedName("gros_elevage") val grosElevage: String,
    @SerializedName("score_biens_menage") val scoreBiensMenage: Int,
    @SerializedName("nbre_repas_par_jour") val nbreRepasParJour: String?,
    @SerializedName("nbre_consommation_aliements_non_preferes_7jrs") val nbreConsommationAlimentsNonPreferes7jrs: Int,
    @SerializedName("aide_pour_manger_7jrs") val aidePourManger7jrs: String,
    @SerializedName("emprunts_pour_manger_7jrs") val empruntsPourManger7jrs: Int,
    @SerializedName("diminuer_quantite_repas_7jrs") val diminuerQuantiteRepas7jrs: Int,
    @SerializedName("limiter_consommation_adultes_7jrs") val limiterConsommationAdultes7jrs: Int,
    @SerializedName("diminuer_nbre_repas_7jrs") val diminuerNbreRepas7jrs: Int,
    @SerializedName("nbre_utiliser_enfant_pour_manger") val nbreUtiliserEnfantPourManger: Int,
    @SerializedName("autres_infos_alimentation") val autresInfosAlimentation: String?,
    @SerializedName("score_alimentation") val scoreAlimentation: Int,
    @SerializedName("commentaires_suggestions") val commentairesSuggestions: String?,
    @SerializedName("num_ticket") val numTicket: String?,
    @SerializedName("geopoint_ben") val geopointBen: String?,
    @SerializedName("latitude_ben") val latitude: String?,
    @SerializedName("longitude_ben") val longitude: String?,
    @SerializedName("altitude_ben") val altitude: String?,
    @SerializedName("precision_ben") val precisionBen: String?,
    @SerializedName("sequences") val sequences: String?,
    @SerializedName("ben_status") val benStatus: String?,
    @SerializedName("remark_ben") val remarkBen: String?,
    @SerializedName("photo") val photo: String?,
    @SerializedName("auteur") val auteur: String,
    @SerializedName("qr_code") val qrCode: String?,
    @SerializedName("score_statut_matrim") val scoreStatutMatrim: Int,
    @SerializedName("score_demographique") val scoreDemographique: Int,
    @SerializedName("score_final") val scoreFinal: Int
)

data class BeneficiaryResponse(
    val status: String,
    val message: String,
    val data: BeneficiaryResponseData?
)

data class BeneficiaryResponseData(
    val id_ben: Long,
    val codeBenef: String
)

// ---------------- SITE ----------------
data class Site(
    @SerializedName("warehouse_id") val warehouseId: Int,
    @SerializedName("name_warehouse") val nameWarehouse: String
)

data class SiteResponse(
    val status: String,
    val message: String,
    val data: List<Site>
)

data class DistributionRequest(
    @SerializedName("fullname") val fullname: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("numberofchildren") val numberOfChildren: Int?,
    @SerializedName("photo") val photo: String?,
    @SerializedName("landarea") val landArea: String?,   // ⚡ corrigé en String
    @SerializedName("seed") val seed: String?,
    @SerializedName("fertilizer") val fertilizer: String?,
    @SerializedName("maize_qty") val maizeQty: Int?,
    @SerializedName("rice_qty") val riceQty: Int?,
    @SerializedName("cassava_qty") val cassavaQty: Int?,
    @SerializedName("soybean_qty") val soybeanQty: Int?,
    @SerializedName("dap_qty") val dapQty: Int?,
    @SerializedName("kcl_qty") val kclQty: Int?,
    @SerializedName("uree_qty") val ureeQty: Int?,
    @SerializedName("npk") val npk: Int?,
    @SerializedName("suggestion") val suggestion: String?,
    @SerializedName("warehouse_id") val warehouseId: Int,
    @SerializedName("beneficiarie_id") val beneficiarieId: Int? = null, // ⚡ optionnel
    @SerializedName("users_id") val usersId: Int,
    @SerializedName("latitude_wrhs") val latitudeWrhs: Double?,
    @SerializedName("longitude_wrhs") val longitudeWrhs: Double?,
    @SerializedName("altitude_wrhs") val altitudeWrhs: Double?,
    @SerializedName("precision_wrhs") val precisionWrhs: Double?
)

data class DistributionResponse(
    val status: String,
    val message: String,
    val data: Any? = null
)

data class DistributionDto(
    @SerializedName("fullname") val fullname: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("numberofchildren") val numberOfChildren: Int?,
    @SerializedName("photo") val photo: String?,
    @SerializedName("landarea") val landArea: String?,
    @SerializedName("seed") val seed: String?,
    @SerializedName("fertilizer") val fertilizer: String?,
    @SerializedName("maize_qty") val maizeQty: Int?,
    @SerializedName("rice_qty") val riceQty: Int?,
    @SerializedName("cassava_qty") val cassavaQty: Int?,
    @SerializedName("soybean_qty") val soybeanQty: Int?,
    @SerializedName("dap_qty") val dapQty: Int?,
    @SerializedName("kcl_qty") val kclQty: Int?,
    @SerializedName("uree_qty") val ureeQty: Int?,
    @SerializedName("npk") val npk: Int?,
    @SerializedName("suggestion") val suggestion: String?,
    @SerializedName("warehouse_id") val warehouseId: Int,
    @SerializedName("beneficiarie_id") val beneficiarieId: Int?,
    @SerializedName("users_id") val usersId: Int,
    @SerializedName("latitude_wrhs") val latitudeWrhs: Double?,
    @SerializedName("longitude_wrhs") val longitudeWrhs: Double?,
    @SerializedName("altitude_wrhs") val altitudeWrhs: Double?,
    @SerializedName("precision_wrhs") val precisionWrhs: Double?
)

data class DistributionListResponse(
    val status: String,
    val message: String,
    val data: List<DistributionDto>?
)


// ---------------- API SERVICE ----------------
interface ApiService {

    @GET("connectionapi.php")
    suspend fun login(
        @Query("username") username: String,
        @Query("password_hash") password: String
    ): Response<ApiResponse>

    @GET("villageget.php")
    suspend fun getVillages(): Response<VillageResponse>

    @GET("warehouse/read.php")
    suspend fun getWarehouse(): Response<WarehouseResponse>

    @POST("warehouse/create.php")
    suspend fun createWarehouse(
        @Body warehouse: WarehouseRequest
    ): Response<WarehouseResponse>

    @POST("package/create.php")
    suspend fun createColis(
        @Body colis: ColisRequest
    ): Response<ColisResponse>

    @POST("beneficiaries/create.php")
    suspend fun createBeneficiary(
        @Body request: BeneficiaryRequest
    ): Response<BeneficiaryResponse>


    @GET("users/readAllSites.php")
    suspend fun getSites(): Response<SiteResponse>

    @POST("survey_dist/create.php")
    suspend fun createDistribution(
        @Body distribution: DistributionRequest
    ): Response<DistributionResponse>

    @GET("survey_dist/read")
    suspend fun getDistributions(): Response<DistributionResponse>
}
