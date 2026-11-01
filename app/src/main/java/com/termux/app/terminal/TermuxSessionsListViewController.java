package com.termux.app.terminal;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.termux.R;
import com.termux.app.TermuxActivity;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalSession;

import java.util.List;

public class TermuxSessionsListViewController extends ArrayAdapter<TermuxSession> implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    final TermuxActivity mActivity;

    public TermuxSessionsListViewController(TermuxActivity activity, List<TermuxSession> sessionList) {
        super(activity.getApplicationContext(), R.layout.item_terminal_sessions_list, sessionList);
        this.mActivity = activity;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View sessionRowView = convertView;
        if (sessionRowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            sessionRowView = inflater.inflate(R.layout.item_terminal_sessions_list, parent, false);
        }

        TextView sessionTitleView = sessionRowView.findViewById(R.id.session_title);
        TextView sessionSubtitleView = sessionRowView.findViewById(R.id.session_subtitle);
        TextView sessionNumberView = sessionRowView.findViewById(R.id.session_number);
        sessionNumberView.setText(String.valueOf(position + 1));

        TerminalSession sessionAtRow = getItem(position).getTerminalSession();
        if (sessionAtRow == null) {
            sessionTitleView.setText(R.string.session_unavailable);
            sessionSubtitleView.setText("");
            sessionRowView.setActivated(false);
            return sessionRowView;
        }

        String name = sessionAtRow.mSessionName;
        String sessionTitle = sessionAtRow.getTitle();
        boolean sessionRunning = sessionAtRow.isRunning();
        sessionTitleView.setText(!TextUtils.isEmpty(name) ? name :
            mActivity.getString(R.string.session_numbered_title, position + 1));
        sessionSubtitleView.setText(sessionRunning ? (!TextUtils.isEmpty(sessionTitle) ? sessionTitle :
            mActivity.getString(R.string.session_running)) :
            mActivity.getString(R.string.session_exited, sessionAtRow.getExitStatus()));
        sessionSubtitleView.setTextColor(ContextCompat.getColor(mActivity,
            sessionRunning || sessionAtRow.getExitStatus() == 0 ? com.termux.shared.R.color.terminal_text_secondary : com.termux.shared.R.color.terminal_error));
        sessionRowView.setActivated(sessionAtRow == mActivity.getCurrentSession());
        return sessionRowView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TermuxSession clickedSession = getItem(position);
        mActivity.getTermuxTerminalSessionClient().setCurrentSession(clickedSession.getTerminalSession());
        mActivity.getDrawer().closeDrawers();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final TermuxSession selectedSession = getItem(position);
        mActivity.getTermuxTerminalSessionClient().renameSession(selectedSession.getTerminalSession());
        return true;
    }

}
