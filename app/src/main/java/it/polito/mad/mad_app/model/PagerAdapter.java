package it.polito.mad.mad_app.model;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.polito.mad.mad_app.ActivitiesFragment;
import it.polito.mad.mad_app.GroupsFragment;
import it.polito.mad.mad_app.R;

/**
 * Created by Lucia on 11/05/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment frag;
        switch (i) {
            case 0:
                frag = new GroupsFragment();
                //fab.setVisibility(View.VISIBLE);
                break;
            case 1:
                frag = new ActivitiesFragment();

                //fab.setVisibility(View.INVISIBLE);
                break;
            default:
                frag = new GroupsFragment();
                //fab.setVisibility(View.VISIBLE);
                break;
        }
        /*Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
        fragment.setArguments(args)*/
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String s;
        switch (position) {
            case 0:
                s="GROUPS";
                break;
            case 1:
                s="ACTIVITY";
            break;
            default:
                s="GROUPS";
                break;
        }
        return s;
    }
}


