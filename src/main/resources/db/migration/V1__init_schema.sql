-- Create loan_applications table to store application details and final decision
CREATE TABLE loan_applications (
    id UUID PRIMARY KEY,
    monthly_income NUMERIC(15, 2) NOT NULL,
    credit_score INTEGER NOT NULL,
    existing_monthly_debt NUMERIC(15, 2) NOT NULL,
    requested_loan_amount NUMERIC(15, 2) NOT NULL,
    employment_duration_months INTEGER NOT NULL,
    employment_status VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    interest_rate_tier VARCHAR(20),
    interest_rate NUMERIC(5, 2),
    created_at TIMESTAMP NOT NULL
);

-- Create audit_records table to store evaluation results for each individual rule
CREATE TABLE audit_records (
    id UUID PRIMARY KEY,
    loan_application_id UUID NOT NULL REFERENCES loan_applications(id) ON DELETE CASCADE,
    rule_name VARCHAR(100) NOT NULL,
    passed BOOLEAN NOT NULL,
    reason VARCHAR(255) NOT NULL,
    evaluated_at TIMESTAMP NOT NULL
);
