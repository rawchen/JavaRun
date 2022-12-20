package com.rawchen.javarun.modules.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

/**
 * @author RawChen
 * @date 2022-02-28 17:39
 */
@Controller
public class QRCodeController {

	/**
	 * 生成二维码
	 *
	 * @param data
	 * @param height
	 * @param width
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(path = "/api/qrcode", method = RequestMethod.GET)
	@ResponseBody
	public void createQRCode(@RequestParam(name = "url") String data,
							 @RequestParam(name = "h", defaultValue = "290", required = false) Integer height,
							 @RequestParam(name = "w", defaultValue = "290", required = false) Integer width, HttpServletResponse response) throws Exception {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		int ht = 200;
		int wt = 200;
		BufferedImage image = createImage(data, null == height ? ht : height, null == width ? wt : width);
		// 创建二进制的输出流
		ServletOutputStream sos = response.getOutputStream();
		ImageIO.write(image, "jpeg", sos);
	}

	private static BufferedImage createImage(String content, int ht, int wt) {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 容错L = 7%	M = 15%		Q = 25%		H = 30%
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = null;
		try {
			bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, wt, ht, hints);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		return image;
	}

}
