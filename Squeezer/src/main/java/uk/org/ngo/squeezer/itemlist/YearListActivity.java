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

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import java.util.List;

import uk.org.ngo.squeezer.framework.BaseListActivity;
import uk.org.ngo.squeezer.framework.ItemView;
import uk.org.ngo.squeezer.model.Year;

public class YearListActivity extends BaseListActivity<Year> {

    @Override
    public ItemView<Year> createItemView() {
        return new YearView(this);
    }

    @Override
    protected void registerCallback() throws RemoteException {
        getService().registerYearListCallback(yearListCallback);
    }

    @Override
    protected void unregisterCallback() throws RemoteException {
        getService().unregisterYearListCallback(yearListCallback);
    }

    @Override
    protected void orderPage(int start) throws RemoteException {
        getService().years(start);
    }


    public static void show(Context context) {
        final Intent intent = new Intent(context, YearListActivity.class);
        context.startActivity(intent);
    }

    private final IServiceYearListCallback yearListCallback = new IServiceYearListCallback.Stub() {
        public void onYearsReceived(int count, int start, List<Year> items) throws RemoteException {
            onItemsReceived(count, start, items);
        }
    };

}
