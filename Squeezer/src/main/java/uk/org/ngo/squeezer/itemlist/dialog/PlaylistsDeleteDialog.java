package uk.org.ngo.squeezer.itemlist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import uk.org.ngo.squeezer.R;
import uk.org.ngo.squeezer.itemlist.PlaylistsActivity;

public class PlaylistsDeleteDialog extends DialogFragment {

    private PlaylistsActivity activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        activity = (PlaylistsActivity) getActivity();
        builder.setTitle(getString(R.string.delete_title, activity.getCurrentPlaylist().getName()));
        builder.setMessage(R.string.delete__message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    activity.getService().playlistsDelete(activity.getCurrentPlaylist());
                    activity.clearAndReOrderItems();
                } catch (RemoteException e) {
                    Log.e(getTag(), "Error deleting playlist");
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

}
