package lt.com.water;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public class viewpageAdapter extends PagerAdapter {
    private List<View> list_view;

    public viewpageAdapter(List<View> list_view) {
        this.list_view = list_view;
    }

    @Override
    public int getCount() {
        return list_view.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(list_view.get(position % list_view.size()));
        return list_view.get(position % list_view.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list_view.get(position % list_view.size()));
    }

}