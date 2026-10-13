package com.nutricore.pro

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import com.nutricore.pro.database.NutricoreDatabase
import com.nutricore.pro.repository.AlimentoRepository
import com.nutricore.pro.repository.AtividadeRepository
import com.nutricore.pro.repository.PerfilRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private lateinit var perfilRepository: PerfilRepository
    private lateinit var alimentoRepository: AlimentoRepository
    private lateinit var atividadeRepository: AtividadeRepository

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val db = NutricoreDatabase.getInstance(this)
        perfilRepository = PerfilRepository(db.perfilUsuarioDao())
        alimentoRepository = AlimentoRepository(db.alimentoConsumoDao())
        atividadeRepository = AtividadeRepository(db.atividadeDao())

        webView = WebView(this).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.allowUniversalAccessFromFileURLs = true

            addJavascriptInterface(
                ApiInterface(this@MainActivity, this),
                "AndroidAPI"
            )

            addJavascriptInterface(
                RoomInterface(
                    this@MainActivity,
                    this,
                    perfilRepository,
                    alimentoRepository,
                    atividadeRepository
                ),
                "RoomDB"
            )

            loadUrl("file:///android_asset/index.html")
        }

        setContentView(webView)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}

class ApiInterface(
    private val activity: ComponentActivity,
    private val webView: WebView
) {
    @JavascriptInterface
    fun buscarAlimentos(termo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=${
                    java.net.URLEncoder.encode(termo, "UTF-8")
                }&search_simple=1&action=process&json=1&lc=pt&cc=br&page_size=8&fields=product_name,nutriments,brands"

                val resultado = URL(url).readText()

                withContext(Dispatchers.Main) {
                    val escapado = resultado
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "")
                        .replace("\r", "")
                    webView.evaluateJavascript(
                        "receberResultadoAPI('$escapado')", null
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    webView.evaluateJavascript("receberResultadoAPI(null)", null)
                }
            }
        }
    }
}

class RoomInterface(
    private val activity: ComponentActivity,
    private val webView: WebView,
    private val perfilRepo: PerfilRepository,
    private val alimentoRepo: AlimentoRepository,
    private val atividadeRepo: AtividadeRepository
) {
    @JavascriptInterface
    fun salvarPerfil(
        uid: String, nome: String, peso: Double, altura: Int,
        idade: Int, sexo: String, nivelAtividade: Double,
        objetivo: String, tmb: Int, tdee: Int, metaCalorias: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            perfilRepo.salvar(uid, nome, peso, altura, idade, sexo, nivelAtividade, objetivo, tmb, tdee, metaCalorias)
        }
    }

    @JavascriptInterface
    fun salvarAlimento(
        uid: String, nome: String, quantidade: Double,
        calorias: Int, proteina: Double, carboidrato: Double,
        gordura: Double, refeicao: String, horario: String
    ) {
        val data = LocalDate.now().toString()
        CoroutineScope(Dispatchers.IO).launch {
            alimentoRepo.salvar(uid, nome, quantidade, calorias, proteina, carboidrato, gordura, refeicao, horario, data)
        }
    }

    @JavascriptInterface
    fun salvarAtividade(
        uid: String, nome: String, duracaoMin: Int,
        met: Double, caloriasGastas: Int
    ) {
        val data = LocalDate.now().toString()
        CoroutineScope(Dispatchers.IO).launch {
            atividadeRepo.salvar(uid, nome, duracaoMin, met, caloriasGastas, data)
        }
    }

    @JavascriptInterface
    fun buscarAlimentosDoDia(uid: String) {
        val data = LocalDate.now().toString()
        CoroutineScope(Dispatchers.IO).launch {
            val alimentos = alimentoRepo.buscarPorData(uid, data)
            val json = alimentos.joinToString(",", "[", "]") { a ->
                """{"id":${a.id},"nome":"${a.nome}","quantidade":${a.quantidade},"calorias":${a.calorias},"proteina":${a.proteina},"carboidrato":${a.carboidrato},"gordura":${a.gordura},"refeicao":"${a.refeicao}","horario":"${a.horario}"}"""
            }
            withContext(Dispatchers.Main) {
                webView.evaluateJavascript("receberAlimentosRoom($json)", null)
            }
        }
    }

    @JavascriptInterface
    fun buscarAtividadesDoDia(uid: String) {
        val data = LocalDate.now().toString()
        CoroutineScope(Dispatchers.IO).launch {
            val atividades = atividadeRepo.buscarPorData(uid, data)
            val json = atividades.joinToString(",", "[", "]") { a ->
                """{"id":${a.id},"nome":"${a.nome}","duracaoMin":${a.duracaoMin},"met":${a.met},"caloriasGastas":${a.caloriasGastas}}"""
            }
            withContext(Dispatchers.Main) {
                webView.evaluateJavascript("receberAtividadesRoom($json)", null)
            }
        }
    }

    @JavascriptInterface
    fun deletarAlimento(id: Int, uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            alimentoRepo.deletar(id, uid)
        }
    }

    @JavascriptInterface
    fun deletarAtividade(id: Int, uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            atividadeRepo.deletar(id, uid)
        }
    }
}