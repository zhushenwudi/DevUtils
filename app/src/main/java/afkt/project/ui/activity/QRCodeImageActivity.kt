package afkt.project.ui.activity

import afkt.project.R
import afkt.project.base.app.BaseActivity
import afkt.project.base.config.RouterPath
import afkt.project.databinding.ActivityQrcodeImageBinding
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import dev.base.DevSource
import dev.engine.DevEngine
import dev.engine.barcode.BarCodeResult
import dev.engine.barcode.listener.BarCodeDecodeCallback
import dev.engine.image.listener.BitmapListener
import dev.engine.media.MediaConfig
import dev.utils.DevFinal
import dev.utils.app.*
import dev.utils.app.toast.ToastTintUtils
import dev.utils.common.StringUtils
import dev.utils.common.ThrowableUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * detail: 二维码图片解析
 * @author Ttt
 */
@Route(path = RouterPath.QRCodeImageActivity_PATH)
class QRCodeImageActivity : BaseActivity<ActivityQrcodeImageBinding>() {

    // 图片 Bitmap
    private var selectBitmap: Bitmap? = null

    override fun baseLayoutId(): Int = R.layout.activity_qrcode_image

    override fun initListener() {
        super.initListener()
        ListenerUtils.setOnClicks(
            this,
            binding.vidAqiSelectBtn, binding.vidAqiTv
        )
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.vid_aqi_select_btn -> {
                // 初始化图片配置
                var config = MediaConfig()
                    .setCompress(false).setMaxSelectNum(1).setCrop(false)
                    .setMimeType(MediaConfig.MimeType.ofImage())
                    .setCamera(true).setGif(false)
                // 打开图片选择器
                DevEngine.getMedia()?.openGallery(mActivity, config)
            }
            R.id.vid_aqi_tv -> {
                val text = TextViewUtils.getText(binding.vidAqiTv)
                if (TextUtils.isEmpty(text)) return
                // 复制到剪切板
                ClipboardUtils.copyText(text)
                // 进行提示
                ToastTintUtils.success(ResourceUtils.getString(R.string.str_copy_suc) + " -> " + text)
            }
        }
    }

    // ==========
    // = 图片回传 =
    // ==========

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // 判断是否属于图片选择
        if (resultCode == RESULT_OK && data != null) {
            MainScope().launch() {
                // 获取图片地址
                val imgPath = DevEngine.getMedia()?.getSingleSelectorPath(data, true)

                val source = if (UriUtils.isUri(imgPath)) {
                    DevSource.create(UriUtils.getUriForString(imgPath))
                } else {
                    DevSource.createWithPath(imgPath)
                }
                DevEngine.getImage()?.loadBitmap(
                    mActivity, source, null, object : BitmapListener() {
                        override fun onStart(source: DevSource?) {
                        }

                        override fun onResponse(
                            source: DevSource?,
                            value: Bitmap?
                        ) {
                            // 获取图片 Bitmap
                            selectBitmap = value
                            // 解析图片
                            DevEngine.getBarCode().decodeBarCode(
                                selectBitmap, object : BarCodeDecodeCallback<BarCodeResult> {
                                    override fun onResult(
                                        success: Boolean,
                                        result: BarCodeResult?,
                                        error: Throwable?
                                    ) {
                                        HandlerUtils.postRunnable {
                                            if (success) {
                                                val builder = StringBuilder()
                                                    .append("二维码解析数据: \n")
                                                    .append(
                                                        StringUtils.checkValue(
                                                            DevFinal.NULL_STR,
                                                            result?.getResultData()
                                                        )
                                                    )
                                                TextViewUtils.setText(
                                                    binding.vidAqiTv,
                                                    builder.toString()
                                                )
                                            } else {
                                                TextViewUtils.setText(
                                                    binding.vidAqiTv, "图片非二维码 / 识别失败\n" +
                                                            ThrowableUtils.getThrowableStackTrace(
                                                                error
                                                            )
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        override fun onFailure(
                            source: DevSource?,
                            throwable: Throwable?
                        ) {
                            TextViewUtils.setText(
                                binding.vidAqiTv, "图片非二维码 / 识别失败\n"
                                        + ThrowableUtils.getThrowableStackTrace(throwable)
                            )
                        }
                    }
                )
            }
        }
    }
}