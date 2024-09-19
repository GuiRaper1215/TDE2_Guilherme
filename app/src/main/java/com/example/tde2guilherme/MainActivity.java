package com.example.tde2guilherme;


import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ImageView catImageView;
    private Button fetchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        catImageView = findViewById(R.id.catImageView);
        fetchButton = findViewById(R.id.fetchButton);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chamar a função que busca uma nova imagem de gato
                new FetchCatImageTask().execute();
            }
        });
    }

    // Classe AsyncTask para buscar a imagem de gato em segundo plano
    private class FetchCatImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                // Fazer a requisição à API
                URL url = new URL("https://api.thecatapi.com/v1/images/search");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    StringBuilder jsonText = new StringBuilder();
                    int c;
                    while ((c = in.read()) != -1) {
                        jsonText.append((char) c);
                    }

                    // Parsear o JSON para obter a URL da imagem
                    JSONArray jsonArray = new JSONArray(jsonText.toString());
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String imageUrl = jsonObject.getString("url");

                    // Baixar a imagem da URL
                    URL imageLink = new URL(imageUrl);
                    HttpURLConnection imageConnection = (HttpURLConnection) imageLink.openConnection();
                    imageConnection.connect();
                    InputStream inputStream = imageConnection.getInputStream();
                    return BitmapFactory.decodeStream(inputStream);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                // Definir a imagem baixada no ImageView
                catImageView.setImageBitmap(result);
            } else {
                Toast.makeText(MainActivity.this, "Erro ao carregar imagem!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
