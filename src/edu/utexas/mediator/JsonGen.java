package edu.utexas.mediator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.utexas.mediator.bench.Benchmark;
import edu.utexas.mediator.bench.Prog;
import edu.utexas.mediator.bench.Tran;

public class JsonGen {

  public static void main(String[] args) throws Exception {
    String filename = "bench.json";
    Benchmark bench = createBench();
    Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    gson.toJson(bench, new PrintStream(filename));
    System.out.println("Saved to: " + filename);
  }

  public static Benchmark createBench() {
    Benchmark bench = new Benchmark();
    Prog src = createSourceProg();
    Prog tgt = createTargetProg();
    bench.setSource(src);
    bench.setTarget(tgt);
    return bench;
  }

  public static Prog createSourceProg() {
    Prog src = new Prog();

    List<String> rels = new ArrayList<>();
    rels.add("Member1(int mid1, String name1, String addr1, String email1)");
    src.setRelations(rels);
    List<String> pks = new ArrayList<>();
    pks.add("Member1(mid1)");
    src.setPrimarykeys(pks);
    List<String> fks = new ArrayList<>();
    src.setForeignkeys(fks);

    List<Tran> trans = new ArrayList<>();
    {
      Tran tran = new Tran();
      tran.setSignature("void insertMember(String name, String addr, String email)");
      List<String> body = new ArrayList<>();
      body.add("ins(Member1, (UUID_x1, name, addr, email))");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("void deleteMember(String name)");
      List<String> body = new ArrayList<>();
      body.add("del(Member1, name1=name)");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("void updateMemberInfo(String name, String addr, String email)");
      List<String> body = new ArrayList<>();
      body.add("upd(Member1, name1=name, addr1, addr)");
      body.add("upd(Member1, name1=name, email1, email)");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("void updateMemberAddr(String name, String addr)");
      List<String> body = new ArrayList<>();
      body.add("upd(Member1, name1=name, addr1, addr)");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("void updateMemberEmail(String name, String email)");
      List<String> body = new ArrayList<>();
      body.add("upd(Member1, name1=name, email1, email)");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("List<Tuple> getMemberAddr(String name)");
      List<String> body = new ArrayList<>();
      body.add("pi([addr1], sigma(name1=name, Member1))");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("List<Tuple> getMemberEmail(String name)");
      List<String> body = new ArrayList<>();
      body.add("pi([email1], sigma(name1=name, Member1))");
      tran.setBody(body);
      trans.add(tran);
    }
    src.setTransactions(trans);
    return src;
  }

  public static Prog createTargetProg() {
    Prog tgt = new Prog();

    List<String> rels = new ArrayList<>();
    rels.add("Member2(int mid2, String name2, int afk2, int efk2)");
    rels.add("Address2(int aid2, String addr2)");
    rels.add("Email2(int eid2, String email2)");
    tgt.setRelations(rels);
    List<String> pks = new ArrayList<>();
    pks.add("Member2(mid2)");
    pks.add("Address2(aid2)");
    pks.add("Email2(eid2)");
    tgt.setPrimarykeys(pks);
    List<String> fks = new ArrayList<>();
    fks.add("Member2(afk2) -> Address2(aid2)");
    fks.add("Member2(efk2) -> Email2(eid2)");
    tgt.setForeignkeys(fks);

    List<Tran> trans = new ArrayList<>();
    {
      Tran tran = new Tran();
      tran.setSignature("void insertMember(String name, String addr, String email)");
      List<String> body = new ArrayList<>();
      body.add("ins(Address2, (UUID_x2, addr))");
      body.add("ins(Email2, (UUID_x3, email))");
      body.add("ins(Member2, (UUID_x4, name, UUID_x2, UUID_x3))");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("void deleteMember(String name)");
      List<String> body = new ArrayList<>();
      body.add("del(Address2, in(aid2, pi([afk2], sigma(name2=name, Member2))))");
      body.add("del(Email2, in(eid2, pi([efk2], sigma(name2=name, Member2))))");
      body.add("del(Member2, name2=name)");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("void updateMemberInfo(String name, String addr, String email)");
      List<String> body = new ArrayList<>();
      body.add("upd(Address2, in(aid2, pi([afk2], sigma(name2=name, Member2))), addr2, addr)");
      body.add("upd(Email2, in(eid2, pi([efk2], sigma(name2=name, Member2))), email2, email)");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("void updateMemberAddr(String name, String addr)");
      List<String> body = new ArrayList<>();
      body.add("upd(Address2, in(aid2, pi([afk2], sigma(name2=name, Member2))), addr2, addr)");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("void updateMemberEmail(String name, String email)");
      List<String> body = new ArrayList<>();
      body.add("upd(Email2, in(eid2, pi([efk2], sigma(name2=name, Member2))), email2, email)");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("List<Tuple> getMemberAddr(String name)");
      List<String> body = new ArrayList<>();
      body.add("pi([addr2], sigma(name2=name, join(Member2, Address2)))");
      tran.setBody(body);
      trans.add(tran);
    }
    {
      Tran tran = new Tran();
      tran.setSignature("List<Tuple> getMemberEmail(String name)");
      List<String> body = new ArrayList<>();
      body.add("pi([email2], sigma(name2=name, join(Member2, Email2)))");
      tran.setBody(body);
      trans.add(tran);
    }
    tgt.setTransactions(trans);
    return tgt;
  }

}
