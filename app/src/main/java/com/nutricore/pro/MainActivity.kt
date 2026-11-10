package com.nutricore.pro

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebViewAssetLoader
import com.nutricore.pro.database.NutricoreDatabase
import com.nutricore.pro.repository.AlimentoRepository
import com.nutricore.pro.repository.AtividadeRepository
import com.nutricore.pro.repository.PerfilRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    companion object {
        private const val DOMINIO_APP = "appassets.androidplatform.net"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val db = NutricoreDatabase.getInstance(this)
        val perfilRepository = PerfilRepository(db.perfilUsuarioDao())
        val alimentoRepository = AlimentoRepository(db.alimentoConsumoDao())
        val atividadeRepository = AtividadeRepository(db.atividadeDao())

        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain(DOMINIO_APP)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView = WebView(this).apply {
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val uri = request?.url ?: return null
                    return assetLoader.shouldInterceptRequest(uri)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val req = request ?: return true
                    return req.isForMainFrame && req.url.host != DOMINIO_APP
                }
            }
            webChromeClient = WebChromeClient()

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

            addJavascriptInterface(ApiInterface(this@MainActivity, this), "AndroidAPI")

            addJavascriptInterface(
                RoomInterface(perfilRepository, alimentoRepository, atividadeRepository),
                "RoomDB"
            )

            loadUrl("https://$DOMINIO_APP/assets/index.html")
        }

        val container = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#060709"))
            addView(
                webView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val areas = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout() or
                        WindowInsetsCompat.Type.ime()
            )
            view.setPadding(areas.left, areas.top, areas.right, areas.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val controle = WindowCompat.getInsetsController(window, container)
        controle.isAppearanceLightStatusBars = false
        controle.isAppearanceLightNavigationBars = false

        setContentView(container)
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
                    webView.evaluateJavascript("receberResultadoAPI('$escapado')", null)
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
    private val perfilRepo: PerfilRepository,
    private val alimentoRepo: AlimentoRepository,
    private val atividadeRepo: AtividadeRepository
) {

    private fun <T> executar(padrao: T, bloco: suspend () -> T): T =
        try {
            runBlocking(Dispatchers.IO) { bloco() }
        } catch (e: Exception) {
            Log.e("RoomInterface", "Falha no banco local: ${e.javaClass.simpleName}")
            padrao
        }

    @JavascriptInterface
    fun buscarPerfil(uid: String): String =
        executar("") {
            val p = perfilRepo.buscar(uid)
            if (p == null || p.tmb <= 0) {
                ""
            } else {
                JSONObject().apply {
                    put("name", p.nome)
                    put("age", p.idade)
                    put("weight", p.peso)
                    put("height", p.altura)
                    put("sex", p.sexo)
                    put("act", p.nivelAtividade)
                    put("goal", p.objetivo)
                    put("tmb", p.tmb)
                    put("tdee", p.tdee)
                }.toString()
            }
        }

    @JavascriptInterface
    fun garantirPerfil(uid: String, nome: String): Boolean =
        executar(false) {
            perfilRepo.garantirPerfil(uid, nome)
            true
        }

    @JavascriptInterface
    fun salvarPerfil(
        uid: String, nome: String, peso: Double, altura: Int,
        idade: Int, sexo: String, nivelAtividade: Double,
        objetivo: String, tmb: Int, tdee: Int, metaCalorias: Int
    ): Boolean =
        executar(false) {
            perfilRepo.salvar(uid, nome, peso, altura, idade, sexo, nivelAtividade, objetivo, tmb, tdee, metaCalorias)
            true
        }

    @JavascriptInterface
    fun salvarAlimento(
        uid: String, nome: String, quantidade: Double,
        calorias: Int, proteina: Double, carboidrato: Double,
        gordura: Double, refeicao: String, horario: String
    ): Long =
        executar(-1L) {
            alimentoRepo.salvar(
                uid, nome, quantidade, calorias, proteina, carboidrato,
                gordura, refeicao, horario, LocalDate.now().toString()
            )
        }

    @JavascriptInterface
    fun salvarAtividade(
        uid: String, nome: String, duracaoMin: Int,
        met: Double, caloriasGastas: Int
    ): Long =
        executar(-1L) {
            atividadeRepo.salvar(uid, nome, duracaoMin, met, caloriasGastas, LocalDate.now().toString())
        }

    @JavascriptInterface
    fun buscarAlimentosDoDia(uid: String): String =
        executar("[]") {
            val lista = alimentoRepo.buscarPorData(uid, LocalDate.now().toString())
            val array = JSONArray()
            lista.forEach { a ->
                array.put(
                    JSONObject().apply {
                        put("id", a.id)
                        put("nome", a.nome)
                        put("quantidade", a.quantidade)
                        put("calorias", a.calorias)
                        put("proteina", a.proteina)
                        put("carboidrato", a.carboidrato)
                        put("gordura", a.gordura)
                        put("refeicao", a.refeicao)
                        put("horario", a.horario)
                    }
                )
            }
            array.toString()
        }

    @JavascriptInterface
    fun buscarAtividadesDoDia(uid: String): String =
        executar("[]") {
            val lista = atividadeRepo.buscarPorData(uid, LocalDate.now().toString())
            val array = JSONArray()
            lista.forEach { a ->
                array.put(
                    JSONObject().apply {
                        put("id", a.id)
                        put("nome", a.nome)
                        put("duracaoMin", a.duracaoMin)
                        put("met", a.met)
                        put("caloriasGastas", a.caloriasGastas)
                    }
                )
            }
            array.toString()
        }

    @JavascriptInterface
    fun deletarAlimento(id: Int, uid: String): Boolean =
        executar(false) {
            alimentoRepo.deletar(id, uid)
            true
        }

    @JavascriptInterface
    fun deletarAtividade(id: Int, uid: String): Boolean =
        executar(false) {
            atividadeRepo.deletar(id, uid)
            true
        }
}