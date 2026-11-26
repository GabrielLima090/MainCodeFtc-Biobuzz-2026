
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@TeleOp(name = "Main - Robot Control")
public class Main extends OpMode {


   // Motores de Tração (Mecanum)
   private DcMotor frontLeftMotor;
   private DcMotor backLeftMotor;
   private DcMotor frontRightMotor;
   private DcMotor backRightMotor;


   // Mecanismos: Shooter, Conveyor, Intake e Outtake
   private DcMotor shooterMotor;
   private DcMotor conveyorMotor;
   private DcMotor intakeMotor;
   private DcMotor outtakeMotor;


   // Variável para controlar a aceleração progressiva do shooter
   private double shooterPower = 0.0;
   private static final double RAMP_SPEED = 0.03; // Velocidade com que o shooter acelera ao segurar R2


   @Override
   public void init() {
       // Inicialização dos motores de tração Mecanum
       frontLeftMotor = hardwareMap.get(DcMotor.class, "lf");
       backLeftMotor = hardwareMap.get(DcMotor.class, "lr");
       frontRightMotor = hardwareMap.get(DcMotor.class, "rf");
       backRightMotor = hardwareMap.get(DcMotor.class, "rr");


       // Inversão dos motores do lado direito (necessário para tração Mecanum padrão)
       frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
       backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);


       // Comportamento de parada (Brake) para tração
       frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


       // Inicialização dos mecanismos
       shooterMotor = hardwareMap.get(DcMotor.class, "shooter");
       shooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
       shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


       conveyorMotor = hardwareMap.get(DcMotor.class, "conveyor");
       conveyorMotor.setDirection(DcMotorSimple.Direction.FORWARD);


       intakeMotor = hardwareMap.get(DcMotor.class, "intake");
       intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);


       outtakeMotor = hardwareMap.get(DcMotor.class, "outtake");
       outtakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);


       telemetry.addData("Status", "Inicializado com sucesso!");
       telemetry.update();
   }


   @Override
   public void loop() {
       double y = -gamepad1.left_stick_y; // Avançar / Recuar (invertido no gamepad)
       double x = gamepad1.left_stick_x * 1.1; // Strafe lateral (correção de imperfeição)
       double rx = gamepad1.right_stick_x; // Rotação


       double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);
       double frontLeftPower = (y + x + rx) / denominator;
       double backLeftPower = (y - x + rx) / denominator;
       double frontRightPower = (y - x - rx) / denominator;
       double backRightPower = (y + x - rx) / denominator;


       frontLeftMotor.setPower(frontLeftPower);
       backLeftMotor.setPower(backLeftPower);
       frontRightMotor.setPower(frontRightPower);
       backRightMotor.setPower(backRightPower);




       // 2. SHOOTER E CONVEYOR (Segurar R2 / right_trigger ou right_bumper)


       boolean r2Pressed = (gamepad1.right_trigger > 0.1) || gamepad1.right_bumper;


       if (r2Pressed) {
           shooterPower = Math.min(1.0, shooterPower + RAMP_SPEED);
       } else {
           shooterPower = Math.max(0.0, shooterPower - (RAMP_SPEED * 2));
       }


       shooterMotor.setPower(shooterPower);


       // Quando o shooter atinge a velocidade máxima (>= 0.99), ativa o conveyor
       if (shooterPower >= 0.99) {
           conveyorMotor.setPower(1.0);
       } else {
           conveyorMotor.setPower(0.0);
       }




       // 3. INTAKE (Seta para Baixo) E OUTTAKE (Seta para Cima)
       if (gamepad1.dpad_down) {
           intakeMotor.setPower(1.0);
       } else {
           intakeMotor.setPower(0.0);
       }


       if (gamepad1.dpad_up) {
           outtakeMotor.setPower(1.0);
       } else {
           outtakeMotor.setPower(0.0);
       }


       // TELEMETRIA
       telemetry.addData("Shooter Power", "%.2f", shooterPower);
       telemetry.addData("Conveyor", shooterPower >= 0.99 ? "ATIVO" : "PARADO");
       telemetry.addData("Intake (Dpad Down)", gamepad1.dpad_down ? "LIGADO" : "DESLIGADO");
       telemetry.addData("Outtake (Dpad Up)", gamepad1.dpad_up ? "LIGADO" : "DESLIGADO");
       telemetry.update();
   }
}
