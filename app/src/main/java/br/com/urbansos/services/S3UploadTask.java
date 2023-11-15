package br.com.urbansos.services;

import android.os.AsyncTask;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;

public class S3UploadTask extends AsyncTask<Void, Void, Void> {
    private final String ACCESS_KEY_ID = ConfigManager.getAwsAccessKeyId();
    private final String SECRET_KEY = ConfigManager.getAwsSecretKey();

    private AmazonS3 s3Client;
    private String bucketName = "urbansos.images";
    private String objectKey;
    private File fileToUpload;

    public S3UploadTask(String objectKey, String fileToUpload) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_KEY);
        this.s3Client = new AmazonS3Client(credentials, Region.SA_SaoPaulo.toAWSRegion());
        this.objectKey = objectKey;
        this.fileToUpload = new File(fileToUpload);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try
        {
            // Gera o objeto da requisição
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, this.objectKey, this.fileToUpload);

            // Configurar permissões para tornar o objeto público
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);

            // Envia para o S3
            s3Client.putObject(putObjectRequest);

            // Após o upload, exclui o arquivo localmente - Desabilitado para poder usar a imagem do celular do usuário
            // if (this.fileToUpload.exists())
            // {
            //    this.fileToUpload.delete();
            // }
        }
        catch (AmazonServiceException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
