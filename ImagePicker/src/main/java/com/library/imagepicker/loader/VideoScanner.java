package com.library.imagepicker.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.library.imagepicker.data.MediaFile;
import com.library.imagepicker.provider.ImagePickerProvider;

import java.io.File;

/**
 * 媒体库扫描类(视频)
 * <p>
 * Date: 2018/8/21
 * Time: 上午1:01
 */
public class VideoScanner extends AbsMediaScanner<MediaFile> {

    public static final int ALL_IMAGES_FOLDER = -1;//全部图片

    private Context mContext;

    public VideoScanner(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected Uri getScanUri() {
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_TAKEN
        };
    }

    @Override
    protected String getSelection() {
        return null;
    }

    @Override
    protected String[] getSelectionArgs() {
        return null;
    }

    @Override
    protected String getOrder() {
        return MediaStore.Video.Media.DATE_TAKEN + " desc";
    }

    /**
     * 构建媒体对象
     *
     * @param cursor
     * @return
     */
    @Override
    protected MediaFile parse(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
        Integer folderId = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
        String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        long dateToken = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));

//        Uri uri;
//        if (Build.VERSION.SDK_INT >= 24) {
//            uri = getScanUri().buildUpon().appendPath(id).build();
//            //uri = FileProvider.getUriForFile(getContext(), ImagePickerProvider.getFileProviderName(getContext()), new File(path));
//        } else {
//            uri = Uri.fromFile(new File(path));
//        }

        Uri uri = getScanUri().buildUpon().appendPath(id).build();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setUri(uri);
        mediaFile.setPath(path);
        mediaFile.setMime(mime);
        mediaFile.setFolderId(folderId);
        mediaFile.setFolderName(folderName);
        mediaFile.setDuration(duration);
        mediaFile.setDateToken(dateToken);

        return mediaFile;
    }


}
