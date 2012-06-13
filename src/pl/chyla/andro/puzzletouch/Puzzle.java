package pl.chyla.andro.puzzletouch;

import pl.chyla.andro.activity.ShakeActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Puzzle: a simple game
 */
public class Puzzle extends ShakeActivity {

    public PuzzleView mPuzzleView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.puzzle_layout);

        mPuzzleView = (PuzzleView) findViewById(R.id.puzzle);
        mPuzzleView.setTextView((TextView) findViewById(R.id.text));
        mPuzzleView.setMode(PuzzleView.STATE_RUNNING);
        
    }

    @Override
    protected void executeShakeAction() {
      mPuzzleView.startNewGame();
      
    }
    
}
