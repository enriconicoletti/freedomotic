/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.freedomotic.repository;

import com.freedomotic.model.environment.Environment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author nicoletti
 */
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {


}
