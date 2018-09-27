package com.appnroll.box.ui.components.precomputedtext

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.text.PrecomputedText
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appnroll.box.R
import com.appnroll.box.utils.isAtLeastPie
import kotlinx.android.synthetic.main.fragment_precomputed_text.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference


class PrecomputedTextFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_precomputed_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        longText.movementMethod = ScrollingMovementMethod()
        longPrecomputedText.movementMethod = ScrollingMovementMethod()

        clearTextButton.setOnClickListener {
            longText.text = ""
            longPrecomputedText.text = ""
        }

        showTextButton.setOnClickListener {
            longText.text = LOREM_IPSUM
        }

        if (isAtLeastPie()) {
            preComputeLongText()
        } else {
            preComputeLongTextCompat()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun preComputeLongText() {
        val params = longPrecomputedText.textMetricsParams
        showPrecomputedTextButton.isEnabled = false
        val ref = WeakReference(showPrecomputedTextButton)
        GlobalScope.launch(Dispatchers.Default) {
            val precomputedText = PrecomputedText.create(LOREM_IPSUM_TO_PRE_COMPUTE, params)
            GlobalScope.launch(Dispatchers.Main) {
                ref.get()?.let { button ->
                    button.isEnabled = true
                    button.setOnClickListener {
                        longPrecomputedText.text = precomputedText
                    }
                }
            }
        }
    }

    private fun preComputeLongTextCompat() {
        val params = TextViewCompat.getTextMetricsParams(longPrecomputedText)
        showPrecomputedTextButton.isEnabled = false
        val ref = WeakReference(showPrecomputedTextButton)
        GlobalScope.launch(Dispatchers.Default) {
            val precomputedText = PrecomputedTextCompat.create(LOREM_IPSUM_TO_PRE_COMPUTE, params)
            GlobalScope.launch(Dispatchers.Main) {
                ref.get()?.let { button ->
                    button.isEnabled = true
                    button.setOnClickListener {
                        longPrecomputedText.text = precomputedText
                    }
                }
            }
        }
    }

    companion object {

        const val LOREM_IPSUM =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut sed luctus nunc. Pellentesque quis convallis ex, id ultrices eros. Proin faucibus lectus eu egestas auctor. Ut et augue id augue imperdiet maximus sed in turpis. Praesent pulvinar lobortis condimentum. Nam blandit vehicula nunc, eget rutrum metus vehicula quis. Nam ut cursus tortor. Mauris egestas mollis blandit. Donec vehicula lectus dui. Mauris varius mi in dolor facilisis, sed gravida elit tincidunt. Quisque fermentum lorem vel mauris ultricies, id consectetur dui consectetur. Integer congue sapien a condimentum lobortis.\n" +
                "Integer sapien neque, facilisis quis aliquet et, pulvinar ut tellus. Donec molestie leo quis nibh tincidunt aliquam. Phasellus quam sem, accumsan vitae facilisis eu, vestibulum at ex. Quisque tristique libero quis semper egestas. Mauris mollis vehicula cursus. In quis lacinia risus. Maecenas congue ante sit amet pretium convallis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vivamus a dolor non dolor sollicitudin consectetur non ac ligula. Quisque erat risus, finibus a malesuada non, consectetur sed velit. Integer viverra efficitur scelerisque. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Aenean mattis, diam sed fermentum viverra, purus neque posuere diam, in vehicula est dolor vitae dui. In ut sapien convallis, maximus magna eget, ornare ante. Praesent non enim iaculis, mollis ex vel, congue elit. Pellentesque tempor diam eu quam bibendum dapibus.\n" +
                "Quisque quis hendrerit mi. Morbi gravida augue eu lacus maximus laoreet. Morbi mattis non nunc sed semper. Curabitur malesuada magna in fermentum fermentum. Maecenas bibendum dolor sit amet tortor porta, ut rhoncus nisl malesuada. Nam nec urna risus. Suspendisse maximus ipsum augue, et aliquet sapien iaculis nec. Aenean ultrices varius tellus quis faucibus. Nam porttitor quam vitae quam gravida tristique.\n" +
                "Duis vitae pellentesque urna. Suspendisse non elit tempor, feugiat orci et, semper erat. Aenean quis urna et odio varius facilisis. Proin elementum nisl enim, sed ultricies lectus pretium quis. Pellentesque nec sodales nisl. Suspendisse dapibus diam at vehicula ullamcorper. In tempor lobortis nunc, sit amet dictum mi interdum sit amet Mauris posuere enim sit amet dolor gravida luctus. Proin pellentesque lacinia erat. Mauris sed dolor vel magna blandit tempus ut malesuada felis. Quisque faucibus faucibus dolor nec sodales.\n" +
                "Cras rutrum condimentum libero, a vestibulum ligula blandit vel. Praesent efficitur turpis ut nulla eleifend, ac vestibulum risus sodales. Ut volutpat lorem ac nisi fermentum, sed ultricies mauris consequat. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Donec iaculis, nulla eu venenatis finibus, massa massa viverra magna, ac facilisis velit lorem sit amet urna. Vestibulum lacinia pulvinar diam lacinia commodo."

        const val LOREM_IPSUM_TO_PRE_COMPUTE =
                "Aliquam risus velit, eleifend id posuere vitae, bibendum nec lacus. Interdum et malesuada fames ac ante ipsum primis in faucibus. Mauris dictum ipsum convallis, viverra nibh vitae, tristique lectus. Nullam blandit, nisi nec posuere ultricies, velit risus laoreet nulla, non eleifend elit leo eu turpis. Quisque aliquet, lorem at pharetra condimentum, est arcu euismod est, a interdum dolor nibh eget neque. Duis maximus dui ut elit sollicitudin mattis. Cras euismod a eros vitae pellentesque. Vivamus felis nisl, pharetra vel lacinia eget, tristique eu massa. Integer iaculis justo non lobortis maximus. Sed sed semper turpis. Vivamus varius ac nunc et accumsan.\n" +
                "Curabitur facilisis est et tincidunt lobortis. Vivamus eu leo ut augue egestas gravida vitae nec augue. Nulla a egestas ligula, in pulvinar urna. Integer sed ligula commodo, vulputate urna sit amet, lobortis diam. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Ut convallis eget lorem ac vehicula. Praesent vel ligula eleifend, iaculis nibh vel, varius nisi.\n" +
                "Cras in malesuada elit, in scelerisque purus. Pellentesque maximus volutpat est, nec consequat risus rhoncus at. Sed non iaculis ipsum, sit amet aliquet purus. Suspendisse potenti. Etiam eu rhoncus nisi. Quisque sapien neque, sollicitudin quis ex mattis, pretium tincidunt dolor. Etiam pellentesque elementum consectetur. Sed hendrerit eleifend ornare. Praesent elementum leo neque, in mollis sapien facilisis vel. Sed quis luctus urna. Etiam sollicitudin lacus libero, at egestas elit faucibus lacinia. Phasellus a tellus nec nulla cursus finibus porttitor eget odio.\n" +
                "Fusce posuere eros nec euismod efficitur. Aliquam porta erat est, sed malesuada lorem dapibus id. Quisque commodo vel nibh porttitor blandit. Praesent ultricies orci vel turpis fringilla, quis sagittis nulla dictum. Nam faucibus tristique nisi, sed tincidunt lorem dictum et. Praesent vitae lacinia mi. Morbi ultrices imperdiet turpis a tempor. Pellentesque ante diam, mollis ac euismod eu, elementum non ipsum. Proin a lacus in ligula pretium semper at at sem. Aenean gravida, ante nec iaculis ullamcorper, elit quam sollicitudin nisl, et sodales turpis nulla sed magna. Donec ornare nisl vitae molestie consectetur. Nunc posuere erat arcu, vel eleifend libero laoreet blandit. Ut ultricies dolor non aliquet congue. Nullam in congue est. Curabitur ac felis non dui auctor congue.\n" +
                "Curabitur ex augue, tempus vitae semper in, consequat id magna. Nam at eros blandit, gravida leo tempus, lobortis eros. Cras sagittis posuere erat. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nam vel viverra turpis. Ut pharetra a erat at auctor. Integer sit amet velit eu augue commodo tincidunt non non velit. Aenean tempor quis urna sit amet suscipit. Proin eu magna risus. Praesent eu ultricies velit. Curabitur purus leo, dictum et tincidunt vitae, mattis ut leo."

        fun getInstance() = PrecomputedTextFragment()
    }
}
