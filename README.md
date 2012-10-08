android-image-cache
===================

android bitmap cache, SoftReferences, LruCache

android根据url下载网络图片并缓存
第一层缓存：采用SoftReferences软引用内存缓存Bitmap；（默认缓存bitmap至内存中）
第二层缓存：缓存Bitmap至外部文件中（可设置是否缓bitmap存至文件中，默认不缓存，缓存目录默认为/data/data/packageName/cache/）

调用示例见MainActivity.java