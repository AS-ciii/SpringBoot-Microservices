package com.pm.patientservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {

        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Patient with this email already exists" + patientRequestDTO.getEmail());
        }
        
        Patient patient = patientRepository.save(PatientMapper.fromDTO(patientRequestDTO));
        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient getPatient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient with given ID not found"));
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Patient with this email already exists" + patientRequestDTO.getEmail());
        }

        getPatient.setName(patientRequestDTO.getName());
        getPatient.setAddress(patientRequestDTO.getAddress());
        getPatient.setEmail(patientRequestDTO.getEmail());
        getPatient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        
        Patient updatedPatient = patientRepository.save(getPatient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
