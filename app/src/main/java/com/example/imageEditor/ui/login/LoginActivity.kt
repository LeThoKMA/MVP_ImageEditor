package com.example.imageEditor.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_BIOMETRIC_ENROLL
import android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imageEditor.R
import com.example.imageEditor.databinding.ActivityLoginBinding
import com.example.imageEditor.encrypPreference.MyEncryptPreference
import com.example.imageEditor.ui.main.MainActivity
import com.example.imageEditor.utils.PIN
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {
    private val biometricManager by lazy {
        BiometricManager.from(this)
    }
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val password by lazy {
        MyEncryptPreference(this).getData(PIN)
    }

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    RESULT_OK -> {
                        // Người dùng đã đăng ký sinh trắc học thành công
                        Toast.makeText(
                            this,
                            "Đăng ký sinh trắc học thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    RESULT_CANCELED -> {
                        // Người dùng hủy hoặc không hoàn tất đăng ký
                        Toast.makeText(
                            this,
                            "Đăng ký sinh trắc học bị hủy.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                    }
                }
            }
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
        binding.btnConfirm.setOnClickListener {
            if (password == null) {
                if (binding.edtPassword.text.toString()
                        .isBlank() || binding.edtConfirmPassword.text.toString().isBlank()
                ) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT)
                        .show()
                }
                if (binding.edtPassword.text.toString() != binding.edtConfirmPassword.text.toString()) {
                    Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                } else {
                    MyEncryptPreference(this).saveData(PIN, binding.edtPassword.text.toString())
                }
            } else {
                if (binding.edtPassword.text.toString()
                        .isBlank()
                ) {
                    Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT)
                        .show()
                }
                if (binding.edtPassword.text.toString() != password) {
                    Toast.makeText(this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun handleLogin() {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Thiết bị hỗ trợ sinh trắc học
                onBiometricSuccess()
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // Thiết bị không có phần cứng sinh trắc học
                binding.btnLogin.visibility = View.INVISIBLE
                binding.lnRegister.visibility = View.VISIBLE

                if (password != null) {
                    binding.tvConfirmPassword.visibility = View.GONE
                    binding.edtConfirmPassword.visibility = View.GONE
                }
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // Phần cứng sinh trắc học hiện không khả dụng
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Không có dữ liệu sinh trắc học nào được đăng ký
                val enrollIntent = Intent(ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                launcher?.launch(enrollIntent)
            }
        }
    }


    private fun onBiometricSuccess() {
        val executor: Executor = ContextCompat.getMainExecutor(this)

        // Thiết lập callback cho BiometricPrompt
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(this@LoginActivity, "Xác thực thành công!", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@LoginActivity, "Lỗi: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@LoginActivity,
                        "Xác thực không thành công!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        // Cấu hình thông tin cho BiometricPrompt
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Đăng nhập sinh trắc học")
            .setSubtitle("Sử dụng vân tay hoặc khuôn mặt của bạn")
            .setNegativeButtonText("Hủy")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}
