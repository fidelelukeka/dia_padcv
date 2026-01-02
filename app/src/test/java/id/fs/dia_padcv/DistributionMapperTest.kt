import com.google.gson.Gson
import id.fs.dia_padcv.data.local.entities.Distribution
import id.fs.dia_padcv.data.remote.api.DistributionRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class DistributionMapperTest {

    private val gson = Gson()

    @Test
    fun `toRequest should map Distribution to correct JSON`() {
        // 🔹 Arrange : créer un Distribution avec des valeurs réalistes
        val distribution = Distribution(
            idDistribution = 95,
            nomComplet = "Jean Mukendi",
            sexe = "M",
            phone = "+243812345678",
            tailleMenage = 4,
            image = "base64_encoded_image_string",
            superficie = 2,
            kgMais = 50,
            kgRiz = 20,
            kgManioc = 30,
            kgSoja = 10,
            kgDap = 5,
            kgKcl = 5,
            kgUree = 10,
            kgNpk = 15,
            suggestion = "Besoin d'un suivi agricole",
            siteId = 40,
            usersId = 12,
            latitude = "-4.3256",
            longitude = "15.3222",
            altitude = "312.5",
            precision = "5.0"
        )

        // 🔹 Act : mapper vers DistributionRequest
        val request = distribution.toRequest()
        val json = gson.toJson(request)

        // 🔹 Expected JSON (simplifié pour le test)
        val expectedJson = """
            {
              "id_distribution":95,
              "fullname":"Jean Mukendi",
              "gender":"M",
              "phone":"+243812345678",
              "numberofchildren":4,
              "photo":"base64_encoded_image_string",
              "landarea":2,
              "seed":"maize,rice,cassava,soybean",
              "fertilizer":"dap,kcl,uree,npk",
              "maize_qty":50,
              "rice_qty":20,
              "cassava_qty":30,
              "soybean_qty":10,
              "dap_qty":5,
              "kcl_qty":5,
              "uree_qty":10,
              "npk":15,
              "suggestion":"Besoin d'un suivi agricole",
              "warehouse_id":40,
              "users_id":12,
              "latitude_wrhs":-4.3256,
              "longitude_wrhs":15.3222,
              "altitude_wrhs":312.5,
              "precision_wrhs":5.0
            }
        """.trimIndent()

        // 🔹 Assert : comparer les deux JSON
        assertEquals(
            gson.fromJson(expectedJson, DistributionRequest::class.java),
            request
        )
    }
}