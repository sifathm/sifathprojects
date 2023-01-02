package gitlet;

import java.io.Serializable;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author
 */
public class Main implements Serializable {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
        } else {
            String firstArg = args[0];
            SomeObj bloop = new SomeObj();
            switch (firstArg) {
                default:
                    System.out.println("No command with that name exists.");
                    break;
                case "init":
                    bloop.init();
                    break;
                case "add":
                    String filename = args[1];
                    bloop.add(filename);
                    break;
                case "commit":
                    String message = args[1];
                    bloop.commit(message);
                    break;
                case "checkout":
                    if (args.length == 3) {
                        String toCheckout = args[2];
                        bloop.checkout1(toCheckout);
                    } else if (args.length == 4) {
                        String commitID = args[1];
                        String operand = args[2];
                        String toCommit = args[3];
                        bloop.checkout2(commitID, toCommit, operand);
                    } else if (args.length == 2) {
                        String branchName = args[1];
                        bloop.checkout3(branchName);
                    }
                    break;
                case "log":
                    bloop.log();
                    break;
                case "rm":
                    String rmfile = args[1];
                    bloop.rm(rmfile);
                    break;
                case "branch":
                    String branchname = args[1];
                    bloop.branch(branchname);
                    break;
                case "status":
                    bloop.status();
                    break;
                case "rm-branch":
                    String name = args[1];
                    bloop.rmb(name);
                    break;
                case "global-log":
                    bloop.gl();
                    break;
                case "find":
                    String msg = args[1];
                    bloop.find(msg);
                    break;
                case "reset":
                    String commitID = args[1];
                    bloop.reset(commitID);
                    break;
                case "merge":
                    String bn = args[1];
                    bloop.merge(bn);
                    break;
            }
        }


    }
}
