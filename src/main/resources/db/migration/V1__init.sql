CREATE TABLE departments (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    version BIGINT
);

CREATE TABLE employees (
    id UUID PRIMARY KEY,
    email VARCHAR(180) NOT NULL UNIQUE,
    full_name VARCHAR(180) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(40) NOT NULL,
    department_id UUID,
    active BOOLEAN NOT NULL,
    deleted BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    version BIGINT
);

CREATE TABLE attendance_records (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    day DATE NOT NULL,
    check_in_time TIMESTAMP WITH TIME ZONE,
    check_out_time TIMESTAMP WITH TIME ZONE,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    version BIGINT
);

CREATE TABLE leave_requests (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    type VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    morning_half_day BOOLEAN NOT NULL,
    afternoon_half_day BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    version BIGINT
);

CREATE TABLE payroll_runs (
    id UUID PRIMARY KEY,
    period VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    version BIGINT
);

CREATE TABLE payslips (
    id UUID PRIMARY KEY,
    payroll_run_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    period VARCHAR(10) NOT NULL,
    base_salary NUMERIC(12,2) NOT NULL,
    overtime NUMERIC(12,2) NOT NULL,
    unpaid_leave_deduction NUMERIC(12,2) NOT NULL,
    bonus NUMERIC(12,2) NOT NULL,
    net_pay NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    version BIGINT
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    payroll_run_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID,
    last_modified_by UUID,
    version BIGINT
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    channel VARCHAR(20) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID,
    version BIGINT
);
