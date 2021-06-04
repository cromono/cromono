//router test를 위한 파일입니다.
import { Request, Response, Router } from 'express';

const router: Router = Router();

router.get('/', (req: Request, res: Response) => {
  res.send('test: ' + req.body);
});

router.post('/', (req: Request, res: Response) => {
  res.send(req.body);
});

export const indexController: Router = router;
