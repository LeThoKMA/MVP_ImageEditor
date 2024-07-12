package com.example.imageEditor.model

import androidx.core.text.HtmlCompat
import com.google.gson.annotations.SerializedName

data class CollectionModel(
    @SerializedName("cover_photo")
    val coverPhoto: CoverPhoto,
    @SerializedName("description")
    val description: String?,
    @SerializedName("featured")
    val featured: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("last_collected_at")
    val lastCollectedAt: String,
    @SerializedName("links")
    val links: Links,
    @SerializedName("preview_photos")
    val previewPhotos: List<PreviewPhoto>,
    @SerializedName("private")
    val `private`: Boolean,
    @SerializedName("published_at")
    val publishedAt: String,
    @SerializedName("share_key")
    val shareKey: String,
    @SerializedName("tags")
    val tags: List<Tag>,
    @SerializedName("title")
    val title: String,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user")
    val user: User,
) {
    val descriptionTextShow
        get() =
            description?.let {
                HtmlCompat.fromHtml(
                    "<b>${user.username}</b> $description",
                    HtmlCompat.FROM_HTML_MODE_LEGACY,
                )
            }
    val likeTextShow get() = HtmlCompat.fromHtml(
        "Liked by <b>${coverPhoto.likes} others</b>",
        HtmlCompat.FROM_HTML_MODE_LEGACY,
    )
}
