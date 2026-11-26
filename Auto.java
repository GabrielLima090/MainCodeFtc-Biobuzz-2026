import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@Autonomous(name = "Autonomous Shooter & Conveyor", group = "Autonomous")
public class AutoShoot extends LinearOpMode {


   private DcMotor shooterMotor;
   private DcMotor conveyorMotor;


   @Override
   public void runOpMode() {
       // Inicialização dos motores
       shooterMotor = hardwareMap.get(DcMotor.class, "shooter");
       conveyorMotor = hardwareMap.get(DcMotor.class, "conveyor");


       shooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
       conveyorMotor.setDirection(DcMotorSimple.Direction.FORWARD);


       shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
       conveyorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


       telemetry.addData("Status", "Autônomo Pronto. Aguardando Start...");
       telemetry.update();


       // Aguarda o comando Start no Driver Station
       waitForStart();


       if (opModeIsActive()) {
           // Ativa o shooter na potência máxima
           shooterMotor.setPower(1.0);


           // Opcional: aguarda 1.5 segundos para o shooter atingir a rotação máxima antes de ligar o conveyor
           // sleep(1500);


           // Ativa o conveyor para empurrar o projétil para o shooter
           conveyorMotor.setPower(1.0);


           // Mantém rodando enquanto o opmode estiver ativo
           while (opModeIsActive()) {
               telemetry.addData("Status", "Disparando em andamento...");
               telemetry.addData("Shooter Power", shooterMotor.getPower());
               telemetry.addData("Conveyor Power", conveyorMotor.getPower());
               telemetry.update();
           }


           // Para os motores ao encerrar o autônomo
           shooterMotor.setPower(0.0);
           conveyorMotor.setPower(0.0);
       }
   }
}


