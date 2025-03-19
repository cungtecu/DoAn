import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SigninActivity extends AppCompatActivity {
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);


            if (isPasswordVisible) {
            } else {
            }
            isPasswordVisible = !isPasswordVisible;
        });
    }
}
