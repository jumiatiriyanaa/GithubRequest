package id.ac.polbeng.jumiatiriyana.githubrequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import id.ac.polbeng.jumiatiriyana.githubrequest.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()
    private val request = OkHttpRequest(client)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetInfo.setOnClickListener {
            val loginID = binding.tvSearch.text.toString()
            if (loginID.isEmpty()) {
                Toast.makeText(applicationContext, "Silakan masukkan login ID akun GitHub Anda!", Toast.LENGTH_SHORT).show()
            } else {
                val url = "https://api.github.com/users/$loginID"
                fetchGitHubInfo(url)
            }
        }
    }

    private fun fetchGitHubInfo(strURL: String) {
        request.GET(strURL, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) { // Cek apakah respons berhasil
                    val responseData = response.body?.string()
                    println("Request Successful!!")
                    println(responseData)
                    runOnUiThread {
                        try {
                            if (responseData != null) {
                                val userObject = JSONObject(responseData)
                                val id = userObject.optString("id", "N/A") // Menggunakan optString untuk menghindari JSONException
                                val name = userObject.optString("name", "N/A")
                                val url = userObject.optString("url", "N/A")
                                val blog = userObject.optString("blog", "N/A")
                                val bio = userObject.optString("bio", "N/A")
                                val company = userObject.optString("company", "N/A")
                                binding.tvUserInfo.text =
                                    "${id}\n${name}\n${url}\n${blog}\n${bio}\n${company}"
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Request Failure: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                println("Request Failure.")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.tvSearch.setText("")
        binding.tvSearch.hint = "Enter GitHub username"
    }
}
