package fr.uha.miage;

public enum EnumInterface implements Runnable {
    ACTION_A, ACTION_B;

    @Override
    public void run() {
        System.out.println("Execution de " + this.name());
    }
}

