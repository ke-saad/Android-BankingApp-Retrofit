import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emsi.retrofitbankingapp.api.ApiJSONService
import com.emsi.retrofitbankingapp.api.ApiXMLService
import com.emsi.retrofitbankingapp.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class MainViewModel : ViewModel() {
    private val _comptes = MutableStateFlow<UiState<List<Compte>>>(UiState.Loading)
    val comptes: StateFlow<UiState<List<Compte>>> = _comptes

    var contentType = "application/json"
    private var acceptType = "application/json"
    private lateinit var retrofit: Retrofit
    lateinit var apiJSONService: ApiJSONService
    lateinit var apiXMLService: ApiXMLService

    init {
        setupApiService(GsonConverterFactory.create())
    }

    private fun setupApiService(converterFactory: Converter.Factory) {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request: Request = chain.request()
                .newBuilder()
                .header("Content-Type", contentType)
                .header("Accept", acceptType)
                .build()
            chain.proceed(request)
        }.build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8082/")
            .client(client)
            .addConverterFactory(converterFactory)
            .build()

        apiJSONService = retrofit.create(ApiJSONService::class.java)
        apiXMLService = retrofit.create(ApiXMLService::class.java)
    }

    fun setFormat(format: String) {
        when (format) {
            "JSON" -> {
                contentType = "application/json"
                acceptType = "application/json"
                setupApiService(GsonConverterFactory.create())
            }

            "XML" -> {
                contentType = "application/xml"
                acceptType = "application/xml"
                setupApiService(SimpleXmlConverterFactory.create())
            }
        }
    }

    fun fetchComptes() {
        viewModelScope.launch {
            _comptes.value = UiState.Loading
            try {
                val comptesList = if (acceptType == "application/json") {
                    val response = apiJSONService.getAllComptesJson()
                    if (response.isSuccessful) response.body() ?: emptyList()
                    else emptyList()
                } else {
                    val response = apiXMLService.getAllComptesXml()
                    if (response.isSuccessful) response.body()?.comptes?.toMutableList()
                        ?: mutableListOf()
                    else mutableListOf()
                }

                _comptes.value = if (comptesList.isEmpty()) {
                    UiState.Error("No accounts found.")
                } else {
                    UiState.Success(comptesList)
                }
            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun deleteCompte(id: Long) {
        viewModelScope.launch {
            try {
                val response = if (acceptType == "application/json") {
                    apiJSONService.deleteCompteJson(id)
                } else {
                    apiXMLService.deleteCompteXml(id)
                }

                if (response.isSuccessful) {
                    _comptes.value = when (val currentState = _comptes.value) {
                        is UiState.Success -> UiState.Success(
                            currentState.data.filter { it.id != id }
                        )

                        else -> currentState
                    }
                } else {
                    _comptes.value = UiState.Error("Failed to delete account.")
                }
            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun createCompte(newCompte: Compte) {
        viewModelScope.launch {
            _comptes.value = UiState.Loading
            try {
                val response = if (acceptType == "application/json") {
                    apiJSONService.createCompteJson(newCompte)
                } else {
                    apiXMLService.createCompteXml(newCompte)
                }

                if (response.isSuccessful) {
                    _comptes.value = when (val currentState = _comptes.value) {
                        is UiState.Success -> UiState.Success(
                            currentState.data + (response.body() ?: newCompte)
                        )

                        else -> currentState
                    }
                } else {
                    _comptes.value = UiState.Error("Failed to create account.")
                }
            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun updateCompte(updatedCompte: Compte) {
        viewModelScope.launch {
            _comptes.value = UiState.Loading
            try {
                val response = if (acceptType == "application/json") {
                    apiJSONService.updateCompteJson(updatedCompte.id, updatedCompte)
                } else {
                    apiXMLService.updateCompteXml(updatedCompte.id, updatedCompte)
                }

                if (response.isSuccessful) {
                    _comptes.value = when (val currentState = _comptes.value) {
                        is UiState.Success -> UiState.Success(
                            currentState.data.map {
                                if (it.id == updatedCompte.id) updatedCompte else it
                            }
                        )

                        else -> currentState
                    }
                } else {
                    _comptes.value = UiState.Error("Failed to update account.")
                }
            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
            }
        }
    }
}