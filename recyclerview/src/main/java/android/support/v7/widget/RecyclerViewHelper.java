/**
 * 
 */

package android.support.v7.widget;

/**
 * @author Kuloud
 */
public class RecyclerViewHelper {
    public static int convertPreLayoutPositionToPostLayout(RecyclerView recyclerView, int position) {
        return recyclerView.mRecycler.convertPreLayoutPositionToPostLayout(position);
    }
}
