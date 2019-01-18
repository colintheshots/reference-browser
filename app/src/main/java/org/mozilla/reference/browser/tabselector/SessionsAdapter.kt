package org.mozilla.reference.browser.tabselector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_session.*
import kotlinx.android.synthetic.main.item_session.view.*
import mozilla.components.browser.session.Session
import org.mozilla.reference.browser.R
import org.mozilla.reference.browser.mvi.ActionBusFactory

class SessionsAdapter(private val bus: ActionBusFactory) : RecyclerView.Adapter<SessionViewHolder>() {
    var sessions: List<Session> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder = SessionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_session, parent, false),
            bus)

    override fun getItemCount(): Int = sessions.size

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) =
        holder.bindSession(sessions[position])

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) return super.onBindViewHolder(holder, position, payloads)
        else {
            val bundle = payloads[0] as Bundle
            for (key in bundle.keySet()) {
                when (key) {
                    "url" -> holder.text_url.text = sessions[position].url
                    "title" -> holder.text_title.text = sessions[position].title
                }
            }
        }
    }

    fun updateSessions(newSessions: List<Session>) {
        val diffResult = DiffUtil.calculateDiff(SessionDiffCallback(sessions, newSessions), true)
        diffResult.dispatchUpdatesTo(this)
        sessions = newSessions
    }
}

class SessionViewHolder(view: View, bus: ActionBusFactory, override val containerView: View? = view)
    : RecyclerView.ViewHolder(view), LayoutContainer {

    var session : Session? = null

    init {
        item_session.setOnClickListener {
            bus.emit(SessionsActions::class.java, SessionsActions.SelectSession(session!!))
        }
        item_session.button_delete.setOnClickListener {
            bus.emit(SessionsActions::class.java, SessionsActions.RemoveSession(session!!))
        }
    }

    fun bindSession(session: Session) {
        this.session = session
        text_url.text = session.url
        text_title.text = session.title
    }
}

class SessionDiffCallback(private val oldSessions: List<Session>, private val newSessions: List<Session>)
    : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldSessions.size
    override fun getNewListSize(): Int = newSessions.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldSessions[oldItemPosition].id == newSessions[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldSessions[oldItemPosition] == newSessions[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldSession = oldSessions[oldItemPosition]
        val newSession = newSessions[newItemPosition]
        val diffBundle = Bundle()
        if (oldSession.url != newSession.url) {
            diffBundle.putString("url", newSession.url)
        }
        if (oldSession.title != newSession.title) {
            diffBundle.putString("title", newSession.title)
        }
        return if (diffBundle.size() == 0) null else diffBundle
    }
}