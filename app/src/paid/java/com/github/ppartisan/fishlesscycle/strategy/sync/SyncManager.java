package com.github.ppartisan.fishlesscycle.strategy.sync;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.util.ReadingUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tom on 25/11/16.
 *
 * Responsible for copying Tank and Reading data from Free version of app to Paid.
 *
 * 1. Check whether free app is installed
 * 2. Copy Tanks from free app ContentProvider.
 * 3. Copy Readings from free app ContentProvider.
 * 4. Confirm data read correctly.
 * 4. Write Tanks and Readings to paid app database.
 * 5. Offer to delete free app.
 *
 */

final class SyncManager {

    private List<Tank> mTanks;
    private List<Reading> mReadings;

    SyncManager() {}

    ArrayList<DisplayData> executeCopy(ContentResolver cr) {

        final ContentProviderClient client =
                cr.acquireContentProviderClient(Contract.FREE_AUTHORITY);

        copyTanks(client);
        copyReadings(client);
        writeTanksAndReadings(cr);

        return buildDisplayData();

    }

    private void writeTanksAndReadings(ContentResolver cr) {
        cr.bulkInsert(Contract.TankEntry.CONTENT_URI, TankUtils.toContentValuesArray(mTanks));
        cr.bulkInsert(Contract.ReadingEntry.CONTENT_URI, ReadingUtils.toContentValuesArray(mReadings));
    }

    private ArrayList<DisplayData> buildDisplayData() {

        final ArrayList<DisplayData> displays = new ArrayList<>(mTanks.size());
        final List<Long> readingIds = new ArrayList<>(mReadings.size());

        for (Reading reading : mReadings) {
            readingIds.add(reading.identifier);
        }

        for (Tank tank : mTanks) {
            final long identifier = tank.identifier;
            displays.add(new DisplayData(tank.name, Collections.frequency(readingIds, identifier)));
        }

        return displays;

    }

    private void copyTanks(ContentProviderClient cr) {
        Cursor c = null;
        try {
            c = cr.query(Contract.TankEntry.FREE_URI, null, null, null, null);
            mTanks = (c==null) ? new ArrayList<Tank>() : TankUtils.getTankList(c);
        } catch (RemoteException e){
            throw new RuntimeException(e);
        } finally {
            if(c != null && !c.isClosed()) c.close();
        }
    }

    private void copyReadings(ContentProviderClient cr) {
        Cursor c = null;
        try {
            c = cr.query(Contract.ReadingEntry.FREE_URI, null, null, null, null);
            mReadings = (c==null) ? new ArrayList<Reading>() : ReadingUtils.getReadingsList(c);
        } catch (RemoteException e){
            throw new RuntimeException(e);
        } finally {
            if(c != null && !c.isClosed()) c.close();
        }
    }

    static final class DisplayData implements Parcelable {

        public final String name;
        final int readingsCount;

        DisplayData(String name, int readingsCount) {
            this.name = name;
            this.readingsCount = readingsCount;
        }

        DisplayData(Parcel in) {
            name = in.readString();
            readingsCount = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeInt(readingsCount);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DisplayData> CREATOR = new Creator<DisplayData>() {
            @Override
            public DisplayData createFromParcel(Parcel in) {
                return new DisplayData(in);
            }

            @Override
            public DisplayData[] newArray(int size) {
                return new DisplayData[size];
            }
        };
    }

}
