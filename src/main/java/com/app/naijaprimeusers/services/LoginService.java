package com.app.naijaprimeusers.services;

import com.app.naijaprimeusers.dtos.AccessDTO;
import com.app.naijaprimeusers.dtos.LoginResponseDTO;
import com.app.naijaprimeusers.dtos.ResetEmailDTO;
import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.Login;

public interface LoginService {

    ResponseDTO add(Login login);
    ResponseDTO verifyCode(String id, int code);
    ResponseDTO resendCode(String id);
    LoginResponseDTO login(AccessDTO accessDTO);
    ResponseDTO updateEmail(ResetEmailDTO resetEmailDTO);
}
