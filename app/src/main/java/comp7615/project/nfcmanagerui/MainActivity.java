package comp7615.project.nfcmanagerui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment currentFragment;
            switch (item.getItemId()) {
                case R.id.navigation_read:
                    currentFragment = ReadFragment.newInstance();
                    displayFragment(currentFragment);
                    return true;
                case R.id.navigation_write:
                    currentFragment = WriteFragment.newInstance();
                    displayFragment(currentFragment);
                    return true;
                case R.id.navigation_other:
                    currentFragment = OtherFragment.newInstance();
                    displayFragment(currentFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        displayFragment(ReadFragment.newInstance());
    }

    protected void displayFragment(Fragment currentFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, currentFragment).commit();
    }

}
