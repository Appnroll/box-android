package com.appnroll.box.ui.components.precomputedtext

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.PrecomputedText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appnroll.box.R
import kotlinx.android.synthetic.main.fragment_precomputed_text_recycler.*
import kotlinx.android.synthetic.main.item_text.view.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference
import java.util.*


class PrecomputedTextRecyclerFragment : Fragment() {

    private val items by lazy { generateItems() }
    private val regularTextAdapter by lazy { RegularTextAdapter(requireContext(), items) }
    private val precomputedTextAdapter by lazy { PrecomputedTextAdapter(requireContext(), items) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_precomputed_text_recycler, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = requireContext()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = regularTextAdapter

        usePrecomputedTextCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recyclerView.adapter = precomputedTextAdapter
            } else {
                recyclerView.adapter = regularTextAdapter
            }
        }
    }

    companion object {

        const val ITEMS_COUNT = 10

        fun getInstance(): Fragment {
            return PrecomputedTextRecyclerFragment()
        }

        private fun generateItems(): Array<String> {
            val generator = RandomStringGenerator()
            val items = arrayOfNulls<String>(ITEMS_COUNT)
            items.forEachIndexed { i, _ ->
                items[i] = "$i - ${generator.randomString(1500)}"
            }
            return items.requireNoNulls()
        }
    }

    private class TextViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private abstract class TextAdapter(val context: Context, val items: Array<String>): RecyclerView.Adapter<TextViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): TextViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_text, parent, false)
            return TextViewHolder(view)
        }

        override fun getItemCount() = ITEMS_COUNT
    }

    private class RegularTextAdapter(context: Context, items: Array<String>): TextAdapter(context, items) {

        override fun onBindViewHolder(viewHolder: TextViewHolder, position: Int) {
            with(viewHolder) {
                itemView.textView.text = items[position]
            }
        }
    }

    private class PrecomputedTextAdapter(context: Context, items: Array<String>): TextAdapter(context, items) {

        override fun onBindViewHolder(viewHolder: TextViewHolder, position: Int) {
            with(viewHolder) {
                val params = itemView.textView.textMetricsParams
                val ref = WeakReference(itemView.textView)
                GlobalScope.launch(Dispatchers.Default) {
                    val precomputedText = PrecomputedText.create(items[position], params)
                    GlobalScope.launch(Dispatchers.Main) {
                        ref.get()?.let {
                            it.text = precomputedText
                        }
                    }
                }
            }
        }
    }

    private class RandomStringGenerator {
        private val source = "0123 4567 89AB CDEF GHIJ KLMN OPQR STUV WXYZ abcd efgh ijkl mnop qrst uvwx yz"
        private var random = Random()

        fun randomString(len: Int): String {
            val sb = StringBuilder(len)
            for (i in 0 until len - random.nextInt(500))
                sb.append(source[random.nextInt(source.length)])
            return sb.toString()
        }
    }
}
