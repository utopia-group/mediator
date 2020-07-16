package edu.utexas.mediator.core;

import java.util.ArrayList;
import java.util.Arrays;

import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.constraint.Context;
import edu.utexas.mediator.constraint.Invariant;
import edu.utexas.mediator.constraint.LAttrList;
import edu.utexas.mediator.constraint.LRelConst;
import edu.utexas.mediator.constraint.LTerm;

/**
 * Manually specify invariants for debugging purpose
 */
public class PerfectSynthesizer implements ISynthesizer {

  private Program src;
  private Program tgt;
  private int benchId;
  private final String sSuf = "_s";
  private final String tSuf = "_t";

  public PerfectSynthesizer(int benchId) {
    this.benchId = benchId;
  }

  @Override
  public Invariant synthesize(Program src, Program tgt) {
    this.src = src;
    this.tgt = tgt;
    switch (benchId) {
    case 1:
      return bench1();
    case 2:
      return bench2();
    case 3:
      return bench3();
    case 4:
      return bench4();
    case 5:
      return bench5();
    case 6:
      return bench6();
    case 7:
      return bench7();
    case 8:
      return bench8();
    case 9:
      return bench9();
    case 10:
      return bench10();
    default:
      throw new RuntimeException("Perfect invariant unspecified");
    }
  }

  private Invariant bench1() {
    Context ctx = new Context();
    LRelConst memberS = ctx.mkRelConst(src.getSchema().getRelDeclByName("MEMBER" + sSuf));
    LRelConst addressS = ctx.mkRelConst(src.getSchema().getRelDeclByName("ADDRESS" + sSuf));
    LRelConst memberT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("MEMBER" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "mname" + sSuf, "address" + sSuf, "city" + sSuf,
          "state" + sSuf, "zipcode" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "mname" + tSuf, "address" + tSuf, "city" + tSuf,
          "state" + tSuf, "zipcode" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, ctx.mkEquiJoin(memberS, addressS,
          ctx.mkAttrConst("afk" + sSuf), ctx.mkAttrConst("aid" + sSuf)));
      LTerm rhs = ctx.mkProject(rhsAttrList, memberT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

  private Invariant bench2() {
    Context ctx = new Context();
    LRelConst employeeS = ctx.mkRelConst(src.getSchema().getRelDeclByName("EMPLOYEE" + sSuf));
    LRelConst projectS = ctx.mkRelConst(src.getSchema().getRelDeclByName("PROJECT" + sSuf));
    LRelConst projEmpS = ctx.mkRelConst(src.getSchema().getRelDeclByName("PROJ_EMP" + sSuf));
    LRelConst employee = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("EMPLOYEE" + tSuf));
    LRelConst address = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("ADDRESS" + tSuf));
    LRelConst phone = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("PHONE" + tSuf));
    LRelConst email = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("EMAIL" + tSuf));
    LRelConst job = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("JOB" + tSuf));
    LRelConst project = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("PROJECT" + tSuf));
    LRelConst projEmp = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("PROJ_EMP" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "eid" + sSuf, "ename" + sSuf, "street" + sSuf,
          "zipcode" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "eid" + tSuf, "ename" + tSuf, "street" + tSuf,
          "zipcode" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, employeeS);
      LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(employee, address,
          ctx.mkAttrConst("aid_fk" + tSuf), ctx.mkAttrConst("aid" + tSuf)));
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "eid" + sSuf, "work_phone" + sSuf, "mobile" + sSuf,
          "fax" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "eid" + tSuf, "work_phone" + tSuf, "mobile" + tSuf,
          "fax" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, employeeS);
      LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(employee, phone,
          ctx.mkAttrConst("phid_fk" + tSuf), ctx.mkAttrConst("phid" + tSuf)));
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "eid" + sSuf, "work_email" + sSuf,
          "personal_email" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "eid" + tSuf, "work_email" + tSuf,
          "personal_email" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, employeeS);
      LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(employee, email,
          ctx.mkAttrConst("emid_fk" + tSuf), ctx.mkAttrConst("emid" + tSuf)));
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "eid" + sSuf, "job_title" + sSuf, "job_desc" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "eid" + tSuf, "job_title" + tSuf, "job_desc" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, employeeS);
      LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(employee, job,
          ctx.mkAttrConst("jid_fk" + tSuf), ctx.mkAttrConst("jid" + tSuf)));
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "eid" + sSuf, "ename" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "eid" + tSuf, "ename" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, employeeS);
      LTerm rhs = ctx.mkProject(rhsAttrList, employee);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "pid" + sSuf, "pname" + sSuf, "pdesc" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "pid" + tSuf, "pname" + tSuf, "pdesc" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, projectS);
      LTerm rhs = ctx.mkProject(rhsAttrList, project);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "peid" + sSuf, "pid_fk" + sSuf, "eid_fk" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "peid" + tSuf, "pid_fk" + tSuf, "eid_fk" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, projEmpS);
      LTerm rhs = ctx.mkProject(rhsAttrList, projEmp);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

  private Invariant bench3() {
    Context ctx = new Context();
    LRelConst subscriberS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Subscriber" + sSuf));
    LRelConst subscriber = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Subscriber" + tSuf));
    LRelConst filter = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Filter" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "sid" + sSuf, "sname" + sSuf, "filter" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "sid" + tSuf, "sname" + tSuf, "params" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, subscriberS);
      LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(subscriber, filter,
          ctx.mkAttrConst("fid_fk" + tSuf), ctx.mkAttrConst("fid" + tSuf)));
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "sid" + sSuf, "sname" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "sid" + tSuf, "sname" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, subscriberS);
      LTerm rhs = ctx.mkProject(rhsAttrList, subscriber);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

  private Invariant bench4() {
    Context ctx = new Context();
    LRelConst empS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Employee" + sSuf));
    LRelConst empIdS = ctx
        .mkRelConst(src.getSchema().getRelDeclByName("EmployeeIdentification" + sSuf));
    LRelConst empT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Employee" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "EmployeeNumber" + sSuf, "Name" + sSuf,
          "PhoneNumber" + sSuf, "Picture" + sSuf, "VoicePrint" + sSuf, "RetinalPrint" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "EmployeeNumber" + tSuf, "Name" + tSuf,
          "PhoneNumber" + tSuf, "Picture" + tSuf, "VoicePrint" + tSuf, "RetinalPrint" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, ctx.mkEquiJoin(empS, empIdS,
          ctx.mkAttrConst("EmployeeNumber" + sSuf), ctx.mkAttrConst("EmployeeNumber_fk" + sSuf)));
      LTerm rhs = ctx.mkProject(rhsAttrList, empT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "EmployeeNumber" + sSuf, "Name" + sSuf,
          "PhoneNumber" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "EmployeeNumber" + tSuf, "Name" + tSuf,
          "PhoneNumber" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, empS);
      LTerm rhs = ctx.mkProject(rhsAttrList, empT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "EmployeeNumber_fk" + sSuf, "Picture" + sSuf,
          "VoicePrint" + sSuf, "RetinalPrint" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "EmployeeNumber" + tSuf, "Picture" + tSuf,
          "VoicePrint" + tSuf, "RetinalPrint" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, empIdS);
      LTerm rhs = ctx.mkProject(rhsAttrList, empT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

  private Invariant bench5() {
    Context ctx = new Context();
    LRelConst customerS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Customer" + sSuf));
    LRelConst accountS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Account" + sSuf));
    LRelConst customerT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Customer" + tSuf));
    LRelConst accountT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Account" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "FirstName" + sSuf, "CustomerID" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "FirstName" + tSuf, "CustomerID" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, customerS);
      LTerm rhs = ctx.mkProject(rhsAttrList, customerT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "AccountID" + sSuf, "CustomerID_fk" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "AccountID" + tSuf, "CustomerID_fk" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, accountS);
      LTerm rhs = ctx.mkProject(rhsAttrList, accountT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "CustomerID" + sSuf, "Balance" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "CustomerID_fk" + tSuf, "Balance" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, customerS);
      LTerm rhs = ctx.mkProject(rhsAttrList, accountT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

  private Invariant bench6() {
    Context ctx = new Context();
    LRelConst customerS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Customer" + sSuf));
    LRelConst customerT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Customer" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "CustomerID" + sSuf, "Fname" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "CustomerID" + tSuf, "FirstName" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, customerS);
      LTerm rhs = ctx.mkProject(rhsAttrList, customerT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

  private Invariant bench7() {
    Context ctx = new Context();
    LRelConst customerS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Customer" + sSuf));
    LRelConst policyS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Policy" + sSuf));
    LRelConst customerT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Customer" + tSuf));
    LRelConst policyT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Policy" + tSuf));
    LRelConst holdsT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Holds" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "CustomerPOID" + sSuf, "Name" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "CustomerPOID" + tSuf, "Name" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, customerS);
      LTerm rhs = ctx.mkProject(rhsAttrList, customerT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "PolicyID" + sSuf, "CustomerPOID_fk" + sSuf,
          "Amount" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "PolicyID" + tSuf, "CustomerPOID_fk" + tSuf,
          "Amount" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, policyS);
      LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(holdsT, policyT,
          ctx.mkAttrConst("PolicyID_fk" + tSuf), ctx.mkAttrConst("PolicyID" + tSuf)));
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

  private Invariant bench8() {
    Context ctx = new Context();
    LRelConst addressS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Address" + sSuf));
    LRelConst stateS = ctx.mkRelConst(src.getSchema().getRelDeclByName("State" + sSuf));
    LRelConst addressT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Address" + tSuf));
    LRelConst stateT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("State" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "Aid" + sSuf, "Street" + sSuf, "City" + sSuf,
          "StatePOID_fk" + sSuf, "PostCode" + sSuf, "CountryCode" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "Aid" + tSuf, "Street" + tSuf, "City" + tSuf,
          "StateCode_fk" + tSuf, "PostCode" + tSuf, "CountryCode" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, addressS);
      LTerm rhs = ctx.mkProject(rhsAttrList, addressT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "StateCode" + sSuf, "StatePOID" + sSuf,
          "Name" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      // special: duplicated attributes
      String[] rhsAttrArray = new String[] { "StateCode" + tSuf, "StateCode" + tSuf,
          "Name" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, stateS);
      LTerm rhs = ctx.mkProject(rhsAttrList, stateT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

  private Invariant bench9() {
    Context ctx = new Context();
    LRelConst addressS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Address" + sSuf));
    LRelConst stateS = ctx.mkRelConst(src.getSchema().getRelDeclByName("State" + sSuf));
    LRelConst addressT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Address" + tSuf));
    LRelConst stateT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("State" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "AddressId" + sSuf, "Street" + sSuf, "City" + sSuf,
          "StateCode_fk" + sSuf, "ZipCode" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "AddressId" + tSuf, "Street" + tSuf, "City" + tSuf,
          "StateCode_fk" + tSuf, "ZipCode" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, addressS);
      LTerm rhs = ctx.mkProject(rhsAttrList, addressT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "StateCode" + sSuf, "Name" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "StateCode" + tSuf, "Name" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, stateS);
      LTerm rhs = ctx.mkProject(rhsAttrList, stateT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

  private Invariant bench10() {
    Context ctx = new Context();
    LRelConst employeeS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Employees" + sSuf));
    LRelConst customerS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Customers" + sSuf));
    LRelConst invoiceS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Invoices" + sSuf));
    LRelConst employeeT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Employees" + tSuf));
    LRelConst customerT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Customers" + tSuf));
    LRelConst invoiceT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Invoices" + tSuf));
    Invariant inv = ctx.mkInvariant();
    {
      String[] lhsAttrArray = new String[] { "EmployeeId" + sSuf, "EmployeeName" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "EmployeeId" + tSuf, "EmployeeName" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, employeeS);
      LTerm rhs = ctx.mkProject(rhsAttrList, employeeT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "InvoiceId" + sSuf, "Name" + sSuf, "Address" + sSuf,
          "CityId" + sSuf, "EmployeeId_fk" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "InvoiceId" + tSuf, "Name" + tSuf, "Address" + tSuf,
          "CityId" + tSuf, "EmployeeId_fk" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, ctx.mkEquiJoin(invoiceS, customerS,
          ctx.mkAttrConst("CustomerId_fk" + sSuf), ctx.mkAttrConst("CustomerId" + sSuf)));
      LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(invoiceT, customerT,
          ctx.mkAttrConst("CustomerId_fk" + tSuf), ctx.mkAttrConst("CustomerId" + tSuf)));
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "InvoiceId" + sSuf, "Name" + sSuf, "Address" + sSuf,
          "EmployeeId_fk" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "InvoiceId" + tSuf, "CustomerName" + tSuf,
          "CustomerAddress" + tSuf, "EmpId_fk" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, ctx.mkEquiJoin(invoiceS, customerS,
          ctx.mkAttrConst("CustomerId_fk" + sSuf), ctx.mkAttrConst("CustomerId" + sSuf)));
      LTerm rhs = ctx.mkProject(rhsAttrList, invoiceT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    {
      String[] lhsAttrArray = new String[] { "InvoiceId" + sSuf, "CustomerId_fk" + sSuf,
          "InvoiceDate" + sSuf };
      LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
      String[] rhsAttrArray = new String[] { "InvoiceId" + tSuf, "CustomerId_fk" + tSuf,
          "InvoiceDate" + tSuf };
      LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
      LTerm lhs = ctx.mkProject(lhsAttrList, invoiceS);
      LTerm rhs = ctx.mkProject(rhsAttrList, invoiceT);
      inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
    }
    return inv;
  }

}
