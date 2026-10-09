package fulguris.browser.tabs

import fulguris.R
import fulguris.browser.WebBrowser
import fulguris.di.configPrefs
import fulguris.extensions.dimen
import fulguris.extensions.inflater
import fulguris.extensions.isDarkTheme
import fulguris.extensions.setImageForTheme
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

/**
 * The adapter for vertical mobile style browser tabs.
 */
class TabsDrawerAdapter(
    webBrowser: WebBrowser
) : TabsAdapter(webBrowser) {

    /**
     * From [RecyclerView.Adapter]
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TabViewHolder {
        val view = viewGroup.context.inflater.inflate(R.layout.tab_list_item, viewGroup, false)
        return TabViewHolder(view, webBrowser) //.apply { setIsRecyclable(false) }
    }

    /**
     * From [RecyclerView.Adapter]
     */
    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.exitButton.tag = position

        val tab = tabList[position]

        holder.txtTitle.text = tab.title
        updateViewHolderAppearance(holder, tab)
        updateViewHolderFavicon(holder, tab.favicon, tab.isForeground)
        updateViewHolderBackground(holder, tab.isForeground)
        // Update our copy so that we can check for changes then
        holder.tab = tab.copy();
    }

    private fun updateViewHolderFavicon(viewHolder: TabViewHolder, favicon: Bitmap, isForeground: Boolean) {
        // Apply filter to favicon if needed
        viewHolder.favicon.setImageForTheme(favicon, (webBrowser as Context).isDarkTheme())
    }

    private fun updateViewHolderBackground(viewHolder: TabViewHolder, isForeground: Boolean) {

        Timber.v("updateViewHolderBackground: $isForeground - ${viewHolder.txtTitle.text}")
        viewHolder.iCardView.apply {
            isChecked = isForeground
            // Adjust tab item height depending of foreground state
            val params = layoutParams
            // Tab height is user defined, the current tab keeps its usual extra height
            val tabHeight = (context.configPrefs.verticalTabHeight * resources.displayMetrics.density).toInt()
            val extra = context.dimen(R.dimen.material_grid_touch_xxlarge) - context.dimen(R.dimen.material_grid_touch_large)
            params.height = if (isForeground) tabHeight + extra else tabHeight
            layoutParams = params
        }

    }

    private fun updateViewHolderAppearance(viewHolder: TabViewHolder, tab: TabViewState) {
        if (tab.isForeground) {
            TextViewCompat.setTextAppearance(viewHolder.txtTitle, R.style.boldText)
            webBrowser.changeToolbarBackground(tab.favicon, tab.themeColor, null)
        } else if (tab.isFrozen) {
            TextViewCompat.setTextAppearance(viewHolder.txtTitle, R.style.italicText)
        }
        else {
            TextViewCompat.setTextAppearance(viewHolder.txtTitle, R.style.normalText)
        }
    }

}
