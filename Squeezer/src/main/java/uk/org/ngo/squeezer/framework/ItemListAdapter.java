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

package uk.org.ngo.squeezer.framework;

import uk.org.ngo.squeezer.util.ImageFetcher;

/**
 * Specialization of {@link ItemAdapter} to be used in {@link BaseListActivity}.
 * <p/>
 * Only difference is that the activity's title is automatically updated to reflect the number of
 * items being shown.
 *
 * @param <T> Denotes the class of the items this class should list
 *
 * @author Kurt Aaholst
 */
public class ItemListAdapter<T extends Item> extends ItemAdapter<T> {

    /**
     * Calls {@link ItemAdapter#BaseAdapter(ItemView, ImageFetcher)}
     */
    public ItemListAdapter(ItemView<T> itemView, ImageFetcher imageFetcher) {
        super(itemView, imageFetcher);
    }

    @Override
    protected void onCountUpdated() {
        getActivity().setTitle(getHeader());
    }

}
