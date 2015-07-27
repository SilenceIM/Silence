package org.smssecure.smssecure.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;

import org.smssecure.smssecure.components.KeyboardAwareLinearLayout.OnKeyboardShownListener;
import org.smssecure.smssecure.util.ServiceUtil;

public class InputAwareLayout extends KeyboardAwareLinearLayout implements OnKeyboardShownListener {
  private InputView current;

  public InputAwareLayout(Context context) {
    this(context, null);
  }

  public InputAwareLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public InputAwareLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    addOnKeyboardShownListener(this);
  }

  @Override public void onKeyboardShown() {
    hideAttachedInput();
  }

  public void show(@NonNull final EditText imeTarget, @NonNull final InputView input) {
    if (isKeyboardOpen()) {
      hideSoftkey(imeTarget, new Runnable() {
        @Override public void run() {
          input.show(getKeyboardHeight(), true);
        }
      });
    } else if (current != null && current.isShowing()) {
      current.hide(true);
      input.show(getKeyboardHeight(), true);
    } else {
      input.show(getKeyboardHeight(), false);
    }

    current = input;
  }

  public InputView getCurrentInput() {
    return current;
  }

  public void hideCurrentInput(EditText imeTarget) {
    if (isKeyboardOpen()) hideSoftkey(imeTarget, null);
    else                  hideAttachedInput();
  }

  public void hideAttachedInput() {
    if (current != null) current.hide(true);
    current = null;
  }

  public boolean isInputOpen() {
    return (isKeyboardOpen() || (current != null && current.isShowing()));
  }

  public void showSoftkey(final EditText inputTarget) {
    postOnKeyboardOpen(new Runnable() {
      @Override public void run() {
        hideAttachedInput();
      }
    });
    inputTarget.post(new Runnable() {
      @Override public void run() {
        inputTarget.requestFocus();
        ServiceUtil.getInputMethodManager(inputTarget.getContext()).showSoftInput(inputTarget, 0);
      }
    });
  }

  private void hideSoftkey(final EditText inputTarget, @Nullable Runnable runAfterClose) {
    if (runAfterClose != null) postOnKeyboardClose(runAfterClose);

    ServiceUtil.getInputMethodManager(inputTarget.getContext())
               .hideSoftInputFromWindow(inputTarget.getWindowToken(), 0);
  }

  public interface InputView {
    void show(int height, boolean immediate);
    void hide(boolean immediate);
    boolean isShowing();
  }
}
