package com.booking.services;

import com.booking.configuration.VNPayConfig;
import com.booking.dto.request.PaymentRequest;
import com.booking.dto.request.VNPayRequest;
import com.booking.models.PaymentModel;
import com.booking.repository.PaymentRepository;
import com.booking.repository.ReservationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public PaymentModel add(PaymentRequest request) {
        var reservation = reservationRepository.findById(String.valueOf(request.getReservation_id())).orElseThrow();
        var payment = paymentRepository.findByTxnRef(request.getTxnRef()).orElse(null);
        if (payment != null) {
            throw new RuntimeException("Payment already exists");
        }
        PaymentModel paymentModel =  new PaymentModel();
        paymentModel.setTxnRef(request.getTxnRef());
        paymentModel.setReservation(reservation);
        paymentModel.setTotal(request.getTotal());
        paymentModel.setStatus("PENDING");
        paymentModel.setProvider(request.getProvider());
        paymentModel.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(paymentModel);
        return paymentModel;
    }

    public String createVNPay(VNPayRequest request) throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "order";
        long amount = 0;
        try {
            amount = request.getTotal() * 100;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ERR: Amount must be a number");
        }

        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.TMN_CODE;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", request.getTxnRef());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan hoa don:" + request.getTxnRef());
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.RETURN_URL);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder queryData = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName);
                hashData.append("=");
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    queryData.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    queryData.append("=");
                    queryData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    hashData.append("&");
                    queryData.append("&");
                }
            }
        }
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.HASH_SECRET, hashData.toString());
        queryData.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return VNPayConfig.VNP_URL + "?" + queryData;
    }

    public int VNPayReturn(HttpServletRequest request){
        Map<String, String> fields = new HashMap<>();

        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        String signValue = VNPayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            String txnRef = fields.get("vnp_TxnRef");
            var payment = paymentRepository.findByTxnRef(txnRef)
                    .orElseThrow(() -> new RuntimeException("Payment Not Found"));
            if (!"PENDING".equals(payment.getStatus())) {
                throw new RuntimeException("Invalid Payment Status");
            }
            long total = Long.parseLong(fields.get("vnp_Amount")) / 100;
            if (payment.getTotal() != total) {
                throw new RuntimeException("Invalid Payment Amount");
            }
            var reservation = reservationRepository
                    .findById(String.valueOf(payment.getReservation().getId()))
                    .orElseThrow();
            String responseCode = fields.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                payment.setStatus("SUCCESS");
                payment.setTransactionNo(fields.get("vnp_TransactionNo"));
                payment.setPaidAt(LocalDateTime.now());
                reservation.setStatus("CONFIRMED");
                paymentRepository.save(payment);
                reservationRepository.save(reservation);
                return 1;
            } else {
                payment.setStatus("FAILED");
                reservation.setStatus("CANCELLED");
                paymentRepository.save(payment);
                reservationRepository.save(reservation);
                return 0;
            }
        } else {
            return -1;
        }
    }
}
