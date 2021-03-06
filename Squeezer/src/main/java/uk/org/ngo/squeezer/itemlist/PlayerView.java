/*
 * Copyright (c) 2011 Kurt Aaholst <kaaholst@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.ngo.squeezer.itemlist;

import android.os.RemoteException;
import android.view.View;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import uk.org.ngo.squeezer.R;
import uk.org.ngo.squeezer.framework.BaseItemView;
import uk.org.ngo.squeezer.model.Player;
import uk.org.ngo.squeezer.util.ImageFetcher;

public class PlayerView extends BaseItemView<Player> {

    private static final Map<String, Integer> modelIcons = initializeModelIcons();

    private final PlayerListActivity mActivity;

    public PlayerView(PlayerListActivity activity) {
        super(activity);

        mActivity = activity;

        setViewParams(EnumSet.of(ViewParams.ICON));
        setLoadingViewParams(EnumSet.of(ViewParams.ICON));
    }

    public void bindView(View view, Player item, ImageFetcher imageFetcher) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.text1.setText(item.getName());
        viewHolder.text1.setTextAppearance(mActivity,
                item.equals(mActivity.getActivePlayer()) ? R.style.SqueezerCurrentTextItem
                        : R.style.SqueezerTextItem);

        viewHolder.icon.setImageResource(getModelIcon(item.getModel()));

        view.setBackgroundResource(
                item.equals(mActivity.getActivePlayer()) ? R.drawable.list_item_background_current
                        : R.drawable.list_item_background_normal);
    }

    public void onItemSelected(int index, Player item) throws RemoteException {
        getActivity().getService().setActivePlayer(item);
        getActivity().finish();
    }

    public String getQuantityString(int quantity) {
        return getActivity().getResources().getQuantityString(R.plurals.player, quantity);
    }

    private static Map<String, Integer> initializeModelIcons() {
        Map<String, Integer> modelIcons = new HashMap<String, Integer>();
        modelIcons.put("baby", R.drawable.icon_baby);
        modelIcons.put("boom", R.drawable.icon_boom);
        modelIcons.put("fab4", R.drawable.icon_fab4);
        modelIcons.put("receiver", R.drawable.icon_receiver);
        modelIcons.put("controller", R.drawable.icon_controller);
        modelIcons.put("sb1n2", R.drawable.icon_sb1n2);
        modelIcons.put("sb3", R.drawable.icon_sb3);
        modelIcons.put("slimp3", R.drawable.icon_slimp3);
        modelIcons.put("softsqueeze", R.drawable.icon_softsqueeze);
        modelIcons.put("squeezeplay", R.drawable.icon_squeezeplay);
        modelIcons.put("transporter", R.drawable.icon_transporter);
        return modelIcons;
    }

    private static int getModelIcon(String model) {
        Integer icon = modelIcons.get(model);
        return (icon != null ? icon : R.drawable.icon_blank);
    }
}
