package com.danhaywood.cfct.service;

import com.danhaywood.cfct.model.AuditTrailComparisonResult;
import com.danhaywood.cfct.model.AuditTrailEntryDescriptor;

import java.util.List;

public interface AuditTrailComparisonService {

    AuditTrailComparisonResult compare(
            List<AuditTrailEntryDescriptor> appAForeground,
            List<AuditTrailEntryDescriptor> appBForeground,
            List<AuditTrailEntryDescriptor> appABackground,
            List<AuditTrailEntryDescriptor> appBBackground);
}
